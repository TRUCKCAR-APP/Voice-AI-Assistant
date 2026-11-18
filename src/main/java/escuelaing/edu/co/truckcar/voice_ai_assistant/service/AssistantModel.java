package escuelaing.edu.co.truckcar.voice_ai_assistant.service;

import java.util.List;

/** Modelo de lenguaje que devuelve un objeto JSON. Separado de Azure para probar el orquestador sin red. */
public interface AssistantModel {

    /** Turno previo de la conversación: lo que dijo el conductor y el JSON que respondió el modelo. */
    record Turn(String driver, String assistant) {
    }

    boolean isAvailable();

    /** Devuelve el contenido (JSON) de la respuesta o lanza una excepción si el modelo no respondió. */
    String complete(String systemPrompt, List<Turn> history, String userMessage);

    AssistantModel DISABLED = new AssistantModel() {
        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public String complete(String systemPrompt, List<Turn> history, String userMessage) {
            throw new IllegalStateException("Azure OpenAI no está configurado");
        }
    };
}
