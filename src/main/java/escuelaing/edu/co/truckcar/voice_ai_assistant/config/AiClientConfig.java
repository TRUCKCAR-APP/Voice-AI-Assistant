package escuelaing.edu.co.truckcar.voice_ai_assistant.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsJsonResponseFormat;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.policy.FixedDelayOptions;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.util.HttpClientOptions;
import escuelaing.edu.co.truckcar.voice_ai_assistant.service.AssistantModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente de Azure OpenAI. Sin endpoint o sin clave (o con los valores de relleno del docker-compose) el modelo queda
 * deshabilitado y el servicio responde UNKNOWN: el Copiloto sigue con su intérprete local.
 */
@Slf4j
@Configuration
public class AiClientConfig {

    @Bean
    public AssistantModel assistantModel(@Value("${azure.openai.endpoint:}") String endpoint,
                                         @Value("${azure.openai.api-key:}") String apiKey,
                                         @Value("${azure.openai.deployment-name:gpt-4.1-mini}") String deployment,
                                         @Value("${azure.openai.timeout:4s}") Duration timeout) {
        if (!isConfigured(endpoint, apiKey)) {
            log.warn("Azure OpenAI sin configurar: el asistente responderá UNKNOWN y el Copiloto usará sus reglas locales");
            return AssistantModel.DISABLED;
        }
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                // Un conductor espera la respuesta en voz alta: un solo intento con tiempo límite corto.
                .retryOptions(new RetryOptions(new FixedDelayOptions(0, Duration.ZERO)))
                .clientOptions(new HttpClientOptions()
                        .setConnectTimeout(Duration.ofSeconds(2))
                        .setResponseTimeout(timeout)
                        .setReadTimeout(timeout))
                .buildClient();
        log.info("Asistente de voz con Azure OpenAI (despliegue {})", deployment);
        return new AzureOpenAiModel(client, deployment);
    }

    static boolean isConfigured(String endpoint, String apiKey) {
        return endpoint != null && !endpoint.isBlank() && !endpoint.contains("example.invalid")
                && apiKey != null && !apiKey.isBlank() && !"sin-configurar".equals(apiKey);
    }

    record AzureOpenAiModel(OpenAIClient client, String deployment) implements AssistantModel {

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public String complete(String systemPrompt, List<Turn> history, String userMessage) {
            List<ChatRequestMessage> messages = new ArrayList<>();
            messages.add(new ChatRequestSystemMessage(systemPrompt));
            for (Turn turn : history) {
                messages.add(new ChatRequestUserMessage(turn.driver()));
                messages.add(new ChatRequestAssistantMessage(turn.assistant()));
            }
            messages.add(new ChatRequestUserMessage(userMessage));
            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setTemperature(0.0);
            options.setMaxTokens(200);
            options.setResponseFormat(new ChatCompletionsJsonResponseFormat());
            ChatCompletions completions = client.getChatCompletions(deployment, options);
            if (completions.getChoices() == null || completions.getChoices().isEmpty()
                    || completions.getChoices().get(0).getMessage() == null
                    || completions.getChoices().get(0).getMessage().getContent() == null) {
                throw new IllegalStateException("Azure OpenAI devolvió una respuesta sin contenido");
            }
            return completions.getChoices().get(0).getMessage().getContent();
        }
    }
}
