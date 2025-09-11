package npng.handdoc.global.config;

import lombok.RequiredArgsConstructor;
import npng.handdoc.telemed.util.SignalingHandler;
import npng.handdoc.telemed.util.naver.SttHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SignalingHandler signalingHandler;
    private final SttHandler sttHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingHandler, "/ws/signaling")
                .setAllowedOrigins("*");
        registry.addHandler(sttHandler, "/ws/stt")
                .setAllowedOrigins("*");
    }
}
