package escuelaing.edu.co.truckcar.voice_ai_assistant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "route-agent-service", url = "${truckcar.services.route-agent}")
public interface RouteAgentClient {

    @GetMapping("/api/v1/routes/status")
    String checkRouteStatus(@RequestParam("driverId") String driverId);
}