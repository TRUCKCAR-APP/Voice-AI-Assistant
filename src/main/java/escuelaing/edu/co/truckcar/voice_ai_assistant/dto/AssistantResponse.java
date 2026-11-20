package escuelaing.edu.co.truckcar.voice_ai_assistant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Intención interpretada. El frontend la ejecuta con las mismas confirmaciones habladas que una orden reconocida en el
 * teléfono: este servicio no llama a ningún otro servicio ni cambia nada por su cuenta.
 *
 * @param intent     una de {@link IntentAction}
 * @param alertType  REPORT: landslide, danger, workshop, police, scale, bridge, rest o traffic
 * @param closure    REPORT de tipo workshop: la vía está cerrada (no solo en obra)
 * @param field      LOAD_INFO: eta, destination, weight o summary
 * @param avoidAhead REROUTE: evitar el tramo de adelante
 * @param reply      frase corta para decir en voz alta (CHAT, UNKNOWN y HELP)
 * @param source     AZURE_OPENAI o NONE (sin modelo configurado o sin respuesta válida)
 * @param place      NAVIGATE: el lugar al que quiere ir, tal como lo nombró (la app lo busca con HERE)
 * @param category   FIND_PLACES: hotel, fuel, restaurant, store, truck_parking o workshop
 * @param cheap      FIND_PLACES: el conductor pide opciones económicas
 * @param scope      NEARBY_ALERTS: around (alrededor de su posición) o route (en la ruta)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssistantResponse(IntentAction intent, String alertType, Boolean closure, String field,
                                Boolean avoidAhead, String reply, String source, String place, String category,
                                Boolean cheap, String scope) {

    public AssistantResponse(IntentAction intent, String alertType, Boolean closure, String field, Boolean avoidAhead,
                             String reply, String source, String place) {
        this(intent, alertType, closure, field, avoidAhead, reply, source, place, null, null, null);
    }

    public AssistantResponse(IntentAction intent, String alertType, Boolean closure, String field, Boolean avoidAhead,
                             String reply, String source) {
        this(intent, alertType, closure, field, avoidAhead, reply, source, null, null, null, null);
    }

    public static final String SOURCE_MODEL = "AZURE_OPENAI";
    public static final String SOURCE_NONE = "NONE";

    public static AssistantResponse unknown(String reply) {
        return new AssistantResponse(IntentAction.UNKNOWN, null, null, null, null, reply, SOURCE_NONE);
    }
}
