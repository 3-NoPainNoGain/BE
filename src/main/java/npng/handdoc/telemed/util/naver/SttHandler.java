package npng.handdoc.telemed.util.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Component
@RequiredArgsConstructor
public class SttHandler extends BinaryWebSocketHandler {

    private final NaverGrpcClient naverGrpcClient;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session){

    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message){

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){

    }
}
