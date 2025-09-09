package npng.handdoc.telemed.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private static final int MAX_PEERS_PER_ROOM = 2; // 1:1만 허용
    private final ObjectMapper om = new ObjectMapper();
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /**
     * [연결 수립 단계]
     * - 클라이언트가 /ws/signaling?roomId=... 로 접속하면 호출된다.
     * - roomId를 읽어 그 방의 접속자 목록에 현재 세션을 추가한다.
     * - 방 정원이 2명을 초과하면 접속을 거절한다.
     * - 방에 2명이 모이는 순간, 두 클라이언트에게 모두 bothReady를 보내되
     *   환자(ROLE_PATIENT)에게만 shouldOffer=true를 내려 offer 시작 주체를 명확히 한다.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // roomId 필수 파라미터
        String roomId = getQueryParam(session.getUri(), "roomId");
        if (roomId == null || roomId.isBlank()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("roomId required"));
            return;
        }

        // 역할(role) 저장: 환자/의사 구분(offer 시작자 판단용)
        String role = getQueryParam(session.getUri(), "role");
        if (role != null) session.getAttributes().put("role", role);

        // 세션 컨텍스트에 roomId 저장
        session.getAttributes().put("roomId", roomId);

        // 방 목록에 현재 세션 추가
        var peers = rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>());
        peers.add(session);

        // 정원 초과 시 접속 차단
        if (peers.size() > MAX_PEERS_PER_ROOM) {
            peers.remove(session);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("room full"));
            return;
        }

        // 두 명이 모이면: bothReady 브로드캐스트
        // - 환자만 shouldOffer=true → 환자 측에서 createOffer() 시작
        if (peers.size() == 2) {
            for (var s : peers) {
                String r = (String) s.getAttributes().get("role");
                boolean shouldOffer = "ROLE_PATIENT".equals(r);
                send(s, json(
                        "type","bothReady",
                        "roomId", roomId,
                        "shouldOffer", shouldOffer
                ));
            }
        }
    }

    /**
     * [시그널링 메시지 릴레이]
     * - 브라우저가 서버로 보낸 WebSocket 텍스트(JSON)를 수신한다.
     * - type(offer/answer/candidate/ice/...) 을 확인하고,
     *   같은 roomId의 "다른" 세션들에게 그대로 전달한다 (내용 가공 없음).
     * - 서버는 브라우저 간 P2P 연결 협상을 위한 "중계자" 역할만 수행한다.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        if (roomId == null) return;

        JsonNode json = om.readTree(message.getPayload());
        String type = json.path("type").asText("");

        switch (type) {
            case "offer":      // SDP offer
            case "answer":     // SDP answer
            case "text":       // 텍스트
            case "candidate":  // ICE 후보(표준 명칭)
            case "ice":        // ICE 후보
            case "leave":      // 종료
            case "renegotiate":// 재협상 트리거
                relayToOthers(roomId, session, message);
                break;
            default:
                // 시그널링 외 메시지는 무시
        }
    }

    /**
     * [연결 종료]
     * - 소켓이 닫히면 방 목록에서 제거한다.
     * - 남은 사람들에게 'leave' 알림을 즉시 전송해 PeerConnection 정리를 유도한다.
     * - 방이 비면 방 자체를 제거한다.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        if (roomId == null) return;

        var list = rooms.get(roomId);
        if (list != null) {
            list.remove(session);

            // 남아있는 피어에게 "leave" 알림 전송
            for (var s : list) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(
                            "{\"type\":\"leave\",\"roomId\":\"" + roomId + "\"}"
                    ));
                }
            }
            if (list.isEmpty()) rooms.remove(roomId);
        }
    }

    /**
     * 특정 클라이언트에게 문자열 메시지를 전송한다.
     * - 서버가 주도적으로 알림을 보낼 때 사용 (ex. bothReady 신호)
     * - 세션이 열려 있는 경우에만 전송한다.
     */
    private void send(WebSocketSession session, String payload) throws Exception {
        if (session.isOpen()) session.sendMessage(new TextMessage(payload));
    }

    /**
     * 간단한 JSON 문자열을 생성한다.
     * - 결과 : json("type","bothReady","roomId","123","shouldOffer",true)
     */
    private String json(Object... kv) throws Exception {
        var node = om.createObjectNode();
        for (int i = 0; i < kv.length; i += 2) {
            String k = String.valueOf(kv[i]);
            Object v = kv[i + 1];
            if (v instanceof Boolean) node.put(k, (Boolean) v);
            else node.put(k, String.valueOf(v));
        }
        return om.writeValueAsString(node);
    }

    /**
     * 같은 roomId에 속한 '다른 클라이언트'에게 메시지를 전달한다.
     * - 보낸 사람은 제외하고, 상대 peer 에게만 그대로 전달한다.
     * - WebRTC 시그널을 중계할 때 사용한다.
     */
    private void relayToOthers(String roomId, WebSocketSession from, TextMessage msg) throws Exception {
        for (var s : rooms.getOrDefault(roomId, new CopyOnWriteArrayList<>())) {
            if (s != from && s.isOpen()) s.sendMessage(msg);
        }
    }

    /**
     * WebSocket 연결 요청 URI 에서 특정 쿼리 파라미터 값을 추출한다.
     * - /ws/signaling?roomId=123 → "123" 추출
     */
    private String getQueryParam(URI uri, String key) {
        if (uri == null || uri.getQuery() == null) return null;
        for (String p : uri.getQuery().split("&")) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key))
                return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
        }
        return null;
    }
}
