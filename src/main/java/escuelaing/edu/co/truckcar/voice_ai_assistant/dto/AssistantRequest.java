package escuelaing.edu.co.truckcar.voice_ai_assistant.dto;

import lombok.Data;

@Data
public class AssistantRequest {
    private String audioBase64;
    private String driverId;
}