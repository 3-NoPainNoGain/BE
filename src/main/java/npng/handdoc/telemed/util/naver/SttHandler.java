package npng.handdoc.telemed.util.naver;

import com.google.protobuf.ByteString;
import com.nbp.cdncp.nest.grpc.proto.v1.NestServiceGrpc;
import com.nbp.cdncp.nest.grpc.proto.v1.NestRequest;
import com.nbp.cdncp.nest.grpc.proto.v1.NestResponse;
import com.nbp.cdncp.nest.grpc.proto.v1.NestConfig;
import com.nbp.cdncp.nest.grpc.proto.v1.NestData;
import com.nbp.cdncp.nest.grpc.proto.v1.RequestType;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SttHandler extends BinaryWebSocketHandler {

    private final NestServiceGrpc.NestServiceStub nestStub;

    private final Map<String, StreamObserver<NestRequest>> reqMap = new ConcurrentHashMap<>();

    // 기본 설정 JSON (필요 시 세션 파라미터로 대체 가능)
    private static final String DEFAULT_CONFIG_JSON = """
        {
          "language": "ko-KR",
          "sampleRate": 16000,
          "encoding": "LINEAR16",
          "enablePartialResult": true
        }
        """;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // gRPC 응답 옵저버
        StreamObserver<NestResponse> respObserver = new StreamObserver<>() {
            @Override
            public void onNext(NestResponse resp) {
                // 인식 결과 contents를 프론트로 전송
                String text = resp.getContents();
                trySendText(session, text);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("gRPC response error (session {}): {}", session.getId(), t.toString());
                safeClose(session, CloseStatus.SERVER_ERROR);
            }

            @Override
            public void onCompleted() {
                log.info("gRPC stream completed (session {})", session.getId());
                safeClose(session, CloseStatus.NORMAL);
            }
        };

        // gRPC 요청 스트림 시작
        StreamObserver<NestRequest> reqObserver = nestStub.recognize(respObserver);

        // 초기 CONFIG 전송
        NestConfig cfg = NestConfig.newBuilder()
                .setConfig(DEFAULT_CONFIG_JSON)
                .build();

        NestRequest configReq = NestRequest.newBuilder()
                .setType(RequestType.CONFIG)
                .setConfig(cfg)
                .build();

        reqObserver.onNext(configReq);

        // 매핑 저장
        reqMap.put(session.getId(), reqObserver);

        log.info("WebSocket connected & gRPC stream opened (session {})", session.getId());
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        StreamObserver<NestRequest> req = reqMap.get(session.getId());
        if (req == null) {
            log.debug("No gRPC request stream for session {}", session.getId());
            return;
        }

        ByteBuffer buf = message.getPayload();
        byte[] audio = new byte[buf.remaining()];
        buf.get(audio);

        // 오디오 청크 전송
        NestData data = NestData.newBuilder()
                .setChunk(ByteString.copyFrom(audio))
                .build();

        NestRequest audioReq = NestRequest.newBuilder()
                .setType(RequestType.DATA)
                .setData(data)
                .build();

        try {
            req.onNext(audioReq);
        } catch (Exception e) {
            log.warn("Failed to send audio chunk to gRPC (session {}): {}", session.getId(), e.toString());
            safeClose(session, CloseStatus.PROTOCOL_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        StreamObserver<NestRequest> req = reqMap.remove(session.getId());
        if (req != null) {
            // gRPC 스트림 종료 신호
            try { req.onCompleted(); } catch (Exception ignored) {}
        }
        log.info("WebSocket closed (session {}, code {})", session.getId(), status);
    }

    // 텍스트 안전 전송
    private void trySendText(WebSocketSession session, String text) {
        if (session == null || !session.isOpen()) return;
        try {
            session.sendMessage(new TextMessage(text));
        } catch (Exception e) {
            log.warn("Failed to send TextMessage (session {}): {}", session.getId(), e.toString());
        }
    }

    // 세이프 클로즈
    private void safeClose(WebSocketSession session, CloseStatus status) {
        if (session == null || !session.isOpen()) return;
        try {
            session.close(status);
        } catch (Exception ignored) {}
    }
}
