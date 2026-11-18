package escuelaing.edu.co.truckcar.voice_ai_assistant.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class VoiceSessionManager {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    public void addSession(String driverId, WebSocketSession session) {
        sessions.put(driverId, session);
    }

    public void removeSession(String driverId) {
        sessions.remove(driverId);
    }
}