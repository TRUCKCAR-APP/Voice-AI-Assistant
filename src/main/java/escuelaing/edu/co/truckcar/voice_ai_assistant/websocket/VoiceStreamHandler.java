package escuelaing.edu.co.truckcar.voice_ai_assistant.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.AssistantRequest;
import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.AssistantResponse;
import escuelaing.edu.co.truckcar.voice_ai_assistant.service.IntentOrchestrator;
import escuelaing.edu.co.truckcar.voice_ai_assistant.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceStreamHandler extends TextWebSocketHandler {

    private final VoiceSessionManager sessionManager;
    private final SpeechToTextService sttService;
    private final IntentOrchestrator orchestrator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String driverId = (String) session.getAttributes().getOrDefault("driverId", "conductor-anonimo");
        sessionManager.addSession(driverId, session);
        log.info("Sesión de voz registrada y activa para conductor: {}", driverId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        AssistantRequest request = objectMapper.readValue(message.getPayload(), AssistantRequest.class);
        String transcript = sttService.transcribeAudio(request.getAudioBase64());
        AssistantResponse response = orchestrator.processIntent(transcript, request.getDriverId());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String driverId = (String) session.getAttributes().getOrDefault("driverId", "conductor-anonimo");
        sessionManager.removeSession(driverId);
        log.info("Sesión de voz cerrada para conductor: {}", driverId);
    }
}