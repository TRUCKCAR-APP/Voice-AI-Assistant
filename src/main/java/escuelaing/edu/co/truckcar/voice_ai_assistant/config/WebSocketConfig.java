package escuelaing.edu.co.truckcar.voice_ai_assistant.config;

import escuelaing.edu.co.truckcar.voice_ai_assistant.websocket.VoiceStreamHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final VoiceStreamHandler voiceStreamHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(voiceStreamHandler, "/ws/voice")
                .setAllowedOrigins("*")
                .addInterceptors(jwtHandshakeInterceptor);
    }
}