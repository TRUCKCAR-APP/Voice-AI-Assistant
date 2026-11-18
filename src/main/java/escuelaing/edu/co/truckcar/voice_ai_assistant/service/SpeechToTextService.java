package escuelaing.edu.co.truckcar.voice_ai_assistant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SpeechToTextService {

    public String transcribeAudio(String audioBase64) {
        log.info("Procesando audio a texto (STT)...");
        return "Tengo un bloqueo en la vía a 2 kilómetros."; // Ejemplo simulado
    }
}