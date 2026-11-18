package escuelaing.edu.co.truckcar.voice_ai_assistant;

import escuelaing.edu.co.truckcar.voice_ai_assistant.service.AssistantModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/** Sin Azure ni Redis el servicio arranca: el modelo queda deshabilitado y la memoria es opcional. */
@SpringBootTest(properties = {"azure.openai.endpoint=", "azure.openai.api-key=", "spring.data.redis.port=1"})
class VoiceAiAssistantApplicationTests {

	@Autowired
	AssistantModel model;

	@Test
	void contextLoadsWithoutAzureNorRedis() {
		assertThat(model.isAvailable()).isFalse();
	}
}
