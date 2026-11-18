package escuelaing.edu.co.truckcar.voice_ai_assistant.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiClientConfigTest {

    @Test
    void placeholdersFromDockerComposeMeanNotConfigured() {
        assertThat(AiClientConfig.isConfigured("https://example.invalid/", "sin-configurar")).isFalse();
        assertThat(AiClientConfig.isConfigured("", "key")).isFalse();
        assertThat(AiClientConfig.isConfigured("https://x.openai.azure.com/", " ")).isFalse();
        assertThat(AiClientConfig.isConfigured("https://x.openai.azure.com/", "key")).isTrue();
    }
}
