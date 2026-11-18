package escuelaing.edu.co.truckcar.voice_ai_assistant.controller;

import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.AssistantResponse;
import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.InterpretRequest;
import escuelaing.edu.co.truckcar.voice_ai_assistant.service.IntentOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Detrás del api-gateway: el gateway valida el JWT y envía el usuario en X-Auth-User (el servicio no publica puertos,
 * solo se alcanza dentro de truckcar-network).
 */
@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final IntentOrchestrator orchestrator;

    public AssistantController(IntentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/interpret")
    public AssistantResponse interpret(@RequestHeader(value = "X-Auth-User", required = false) String userId,
                                       @Valid @RequestBody InterpretRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta la identidad del conductor");
        }
        return orchestrator.interpret(userId.strip(), request);
    }
}
