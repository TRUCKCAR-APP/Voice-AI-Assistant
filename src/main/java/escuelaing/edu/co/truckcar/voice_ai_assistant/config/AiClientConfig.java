package escuelaing.edu.co.truckcar.voice_ai_assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiClientConfig {

    @Bean
    public ChatClient voiceAssistantChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("Eres el asistente de voz por inteligencia artificial de TruckCar. " +
                        "Tu objetivo es ayudar a los conductores de transporte pesado a reportar bloqueos viales, " +
                        "consultar el estado de sus rutas y gestionar confirmaciones de entrega. " +
                        "Tus respuestas deben ser extremadamente concisas, amables y directas, ya que serán reproducidas por audio mientras el usuario conduce.")
                .build();
    }
}