package escuelaing.edu.co.truckcar.voice_ai_assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Memoria corta por conductor en Redis: los últimos turnos durante unos minutos, para que "¿y cuánto me falta?" se
 * entienda después de "¿a dónde voy?". Es opcional: si Redis no responde se sigue sin historial.
 */
@Slf4j
@Service
public class ConversationMemoryService {

    static final String KEY_PREFIX = "voice_context:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final int maxTurns;
    private final Duration ttl;

    public ConversationMemoryService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper,
                                     @Value("${truckcar.voice.memory-turns:3}") int maxTurns,
                                     @Value("${truckcar.voice.memory-ttl:10m}") Duration ttl) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.maxTurns = maxTurns;
        this.ttl = ttl;
    }

    public List<AssistantModel.Turn> history(String driverId) {
        try {
            List<String> raw = redisTemplate.opsForList().range(KEY_PREFIX + driverId, 0, -1);
            List<AssistantModel.Turn> turns = new ArrayList<>();
            if (raw == null) return turns;
            for (String item : raw) {
                turns.add(objectMapper.readValue(item, AssistantModel.Turn.class));
            }
            return turns;
        } catch (Exception e) {
            log.debug("Sin memoria de conversación ({}): se sigue sin historial", e.getMessage());
            return List.of();
        }
    }

    public void remember(String driverId, AssistantModel.Turn turn) {
        if (maxTurns <= 0) return;
        String key = KEY_PREFIX + driverId;
        try {
            redisTemplate.opsForList().rightPush(key, objectMapper.writeValueAsString(turn));
            redisTemplate.opsForList().trim(key, -maxTurns, -1);
            redisTemplate.expire(key, ttl);
        } catch (JsonProcessingException e) {
            log.warn("No se pudo serializar el turno de voz: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("Memoria de conversación no disponible ({}): no se guarda el turno", e.getMessage());
        }
    }
}
