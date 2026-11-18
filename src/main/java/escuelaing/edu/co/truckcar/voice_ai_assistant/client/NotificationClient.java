package escuelaing.edu.co.truckcar.voice_ai_assistant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notifications-service", url = "${truckcar.services.notifications}")
public interface NotificationClient {

    @PostMapping("/api/v1/voice/synthesize")
    Map<String, String> synthesizeVoice(@RequestBody Map<String, String> textPayload);
}