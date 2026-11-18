package escuelaing.edu.co.truckcar.voice_ai_assistant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "load-allocation-service", url = "${truckcar.services.load-allocation}")
public interface LoadAllocationClient {

    @PostMapping("/api/v1/loads/{loadId}/deliver-voice")
    void confirmDeliveryViaVoice(@PathVariable("loadId") String loadId);
}