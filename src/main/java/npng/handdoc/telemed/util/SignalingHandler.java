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
    // roomId -> 해당 방에 접속한 WebSocket 세션들
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /**
     * [연결 수립 단계]
     * - 클라이언트가 /ws/signaling?roomId=...&role=... 로 접속하면 호출된다.
     * - roomId를 읽어 그 방의 접속자 목록에 현재 세션을 추가한다.
     * - 방 정원이 2명을 초과하면 접속을 거절한다.
     * - 방에 2명이 모이는 순간, 두 클라이언트에게 모두 bothReady를 보내되
     *   환자(ROLE_PATIENT)에게만 shouldOffer=true를 내려 offer 시작 주체를 명확히 한다.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 1) roomId 필수 파라미터
        String roomId = getQueryParam(session.getUri(), "roomId");
        if (roomId == null || roomId.isBlank()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("roomId required"));
            return;
        }

        // 2) 역할(role) 저장: 환자/의사 구분(offer 시작자 판단용)
        String role = getQueryParam(session.getUri(), "role");
        if (role != null) session.getAttributes().put("role", role);

        // 3) 세션 컨텍스트에 roomId 저장
        session.getAttributes().put("roomId", roomId);

        // 4) 방 목록에 현재 세션 추가
        var peers = rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>());
        peers.add(session);

        // 5) 정원 초과 시 접속 차단
        if (peers.size() > MAX_PEERS_PER_ROOM) {
            peers.remove(session);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("room full"));
            return;
        }

        // 6) 두 명이 모이면: bothReady 브로드캐스트
        //    - 환자만 shouldOffer=true → 환자 측에서 createOffer() 시작
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
     * - type(offer/answer/candidate/ice/text/...)을 확인하고,
     *   같은 roomId의 "다른" 세션들에게 그대로 전달한다(내용 가공 없음).
     * - 즉, 서버는 브라우저 간 P2P 연결 협상을 위한 "중계자" 역할만 수행한다.
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
            case "text":       // 단순 텍스트(알림/메모 등), 채팅 대용으로 사용 가능
            case "candidate":  // ICE 후보(표준 명칭)
            case "ice":        // ICE 후보(프론트가 이렇게 보낸다면 그대로 허용)
            case "leave":      // 나감 이벤트
            case "renegotiate":// 재협상 트리거(화면공유 등)
                relayToOthers(roomId, session, message); // 보낸 사람 제외, 같은 방 모두에게 포워드
                break;
            default:
                // 시그널링 외 메시지는 무시
        }
    }

    /**
     * [연결 종료]
     * - 소켓이 닫히면 방 목록에서 제거한다.
     * - 방이 비면 방 자체를 제거한다.
     * - (선택) 남은 사람에게 'peer-left'를 보내도록 확장 가능
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        if (roomId == null) return;

        var list = rooms.get(roomId);
        if (list != null) {
            list.remove(session);
            // 필요하면 여기서 남은 사람에게 peer-left 알림 전송 가능
            if(list.isEmpty()) rooms.remove(roomId);
        }
    }

    /** 문자열 payload를 해당 세션으로 전송 (열려있을 때만) */
    private void send(WebSocketSession session, String payload) throws Exception {
        if (session.isOpen()) session.sendMessage(new TextMessage(payload));
    }

    /** 간단 JSON 생성: ("키","값", "키","값", ...) 형태로 전달 */
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

    /** 같은 방의 '다른' 세션들에게 메시지 브로드캐스트(내용 가공 없이 그대로 전달) */
    private void relayToOthers(String roomId, WebSocketSession from, TextMessage msg) throws Exception {
        for (var s : rooms.getOrDefault(roomId, new CopyOnWriteArrayList<>())) {
            if (s != from && s.isOpen()) s.sendMessage(msg);
        }
    }

    /** /ws/signaling?roomId=123 → "123" 추출 */
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
