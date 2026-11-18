package escuelaing.edu.co.truckcar.voice_ai_assistant.dto;

/**
 * Intenciones que el Copiloto del frontend sabe ejecutar (js/services/voiceIntents.js). La IA solo puede elegir una de
 * estas: cualquier otro valor se convierte en {@link #UNKNOWN}.
 */
public enum IntentAction {
    REPORT,
    SOS,
    NEARBY_ALERTS,
    ACCEPT_LOAD,
    REJECT_LOAD,
    START_TRIP,
    CONFIRM_DELIVERY,
    REROUTE,
    REPEAT,
    HOURS,
    LOAD_INFO,
    HELP,
    /** Respuesta hablada sin acción (pregunta general de conducción). */
    CHAT,
    UNKNOWN
}
