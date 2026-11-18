package escuelaing.edu.co.truckcar.voice_ai_assistant.service;

import escuelaing.edu.co.truckcar.voice_ai_assistant.client.LoadAllocationClient;
import escuelaing.edu.co.truckcar.voice_ai_assistant.client.NotificationClient;
import escuelaing.edu.co.truckcar.voice_ai_assistant.client.RouteAgentClient;
import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.AssistantResponse;
import escuelaing.edu.co.truckcar.voice_ai_assistant.dto.IntentAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntentOrchestrator {

    private final ConversationMemoryService memoryService;
    private final NotificationClient notificationClient;
    private final RouteAgentClient routeAgentClient;
    private final LoadAllocationClient loadAllocationClient;
    private final ChatClient chatClient; // IA de Azure

    public AssistantResponse processIntent(String transcript, String driverId) {
        log.info("Analizando intención de: {}", transcript);

        String context = "Nueva conversación.";
        try { context = memoryService.getContext(driverId); }
        catch (Exception e) { log.warn("Redis apagado. Usando contexto en blanco."); }
        IntentAction action = IntentAction.AYUDA_GENERAL;
        if (transcript.toLowerCase().contains("bloqueo")) action = IntentAction.REPORTAR_BLOQUEO;
        else if (transcript.toLowerCase().contains("ruta")) action = IntentAction.CONSULTAR_RUTA;
        String prompt = String.format(
                "Historial breve: %s. \nEl conductor acaba de decir: '%s'. \n" +
                        "Responde de forma muy natural, corta y amigable, sabiendo que el sistema ejecutará la acción: %s.",
                context, transcript, action.name()
        );

        String aiResponseText = "Lo siento, no pude procesar tu solicitud.";
        try {
            aiResponseText = chatClient.prompt().user(prompt).call().content();
        } catch (Exception e) {
            log.error("Error contactando a Azure OpenAI: {}", e.getMessage());
        }

        try { memoryService.saveContext(driverId, context + " | IA: " + aiResponseText); }
        catch (Exception e) { log.trace("Omitiendo guardado en Redis."); }

        String audioResult = "";
        try {
            Map<String, String> pollyResponse = notificationClient.synthesizeVoice(Map.of("text", aiResponseText));
            audioResult = pollyResponse.get("audioUrl");
        } catch (Exception e) {
            log.warn("Servicio Notifications apagado temporalmente. Devolviendo texto sin audio.");
        }

        return AssistantResponse.builder()
                .text(aiResponseText)
                .action(action)
                .audioUrl(audioResult)
                .requiresAction(action == IntentAction.REPORTAR_BLOQUEO)
                .build();
    }
}