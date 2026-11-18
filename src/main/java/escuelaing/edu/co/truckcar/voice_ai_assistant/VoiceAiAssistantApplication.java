package escuelaing.edu.co.truckcar.voice_ai_assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VoiceAiAssistantApplication {
	public static void main(String[] args) {
		SpringApplication.run(VoiceAiAssistantApplication.class, args);
	}
}