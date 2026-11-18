package escuelaing.edu.co.truckcar.voice_ai_assistant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssistantResponse {
    private String text;
    private String audioUrl;
    private IntentAction action;
    private boolean requiresAction;
}