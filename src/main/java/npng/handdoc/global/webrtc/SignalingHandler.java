package npng.handdoc.global.webrtc;

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

    private static final int MAX_PEERS_PER_ROOM = 2; // 2명 정원 (1:1 통화만 허용)
    private final ObjectMapper om = new ObjectMapper();
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /**
     * 웹소켓 연결이 맺어졌을 때 호출됨
     * - 쿼리 스트링에서 roomId를 읽어 같은 방으로 묶는다.
     * - 방 정원이 초과되면 연결이 거절된다.
     * - 두 명이 모이면 준비 되었다고 신호를 보낸다.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 쿼리 스트링에서 roomId 추출
        String roomId = getQueryParam(session.getUri(), "roomId");
        if (roomId == null || roomId.isBlank()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("roomId required"));
            return;
        }

        // 세션에 roomId 저장
        session.getAttributes().put("roomId", roomId);

        // 방에 세션 추가
        var peers = rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>());
        peers.add(session);

        // 방에 2명을 초과하면 거절
        if (peers.size() > MAX_PEERS_PER_ROOM) {
            peers.remove(session);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("room full"));
            return;
        }

        // 양쪽에 준비 완료 되면 신호 전달
        if (peers.size() == MAX_PEERS_PER_ROOM) {
            var bothReady = new TextMessage("{\"type\":\"bothReady\",\"roomId\":\"" + roomId + "\"}");
            for (var s : peers) if (s.isOpen()) s.sendMessage(bothReady);
        }
    }

    /**
     * 클라이언트가 보낸 텍스트 메시지 (JSON)을 수신했을 때 호출
     * - type 필드를 읽어 시그널 메시지를 같은 방의 상대에게 전달
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 들어온 메시지의 type을 보고 허용된 것만 상대에게 전달
        String roomId = (String) session.getAttributes().get("roomId");
        if (roomId == null) return;

        JsonNode json = om.readTree(message.getPayload());
        String type = json.path("type").asText("");

        switch (type) {
            case "offer":
            case "answer":
            case "candidate":
            case "leave":
            case "renegotiate":
                relayToOthers(roomId, session, message);
                break;
            default:
        }
    }

    /**
     * 웹 소켓 연결이 종료되었을 때 호출
     * - 방에서 세션 제거
     * - 방이 비면 방 자체를 제거
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션 제거
        String roomId = (String) session.getAttributes().get("roomId");
        if (roomId == null) return;

        var list = rooms.get(roomId);
        if (list != null) {
            list.remove(session);
            if(list.isEmpty()) rooms.remove(roomId);
        }
    }

    /**
     * 같은 방의 사람들에게 메시지 전달
     */
    private void relayToOthers(String roomId, WebSocketSession from, TextMessage msg) throws Exception {
        for (var s : rooms.getOrDefault(roomId, new CopyOnWriteArrayList<>())) {
            if (s != from && s.isOpen()) s.sendMessage(msg);
        }
    }

    /**
     * URI 쿼리 스트링에서 key에 해당하는 값을 추출
     * - /ws/signaling?roomId=123 -> 123을 추출
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