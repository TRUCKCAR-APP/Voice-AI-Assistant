package escuelaing.edu.co.truckcar.voice_ai_assistant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ConversationMemoryService {

    private final StringRedisTemplate redisTemplate;
    public void saveContext(String driverId, String context) {
        redisTemplate.opsForValue().set("voice_context:" + driverId, context, Duration.ofMinutes(10));
    }

    public String getContext(String driverId) {
        String context = redisTemplate.opsForValue().get("voice_context:" + driverId);
        return context != null ? context : "Nueva conversación.";
    }
}