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
    /** Ir a un lugar que no es el destino de la carga: la app lo busca y ofrece las opciones. */
    NAVIGATE,
    /**
     * Un tipo de lugar para parar (comer, descansar, dormir, tanquear, baño, parquear la mula, taller…): con ruta, la
     * app busca sobre la ruta y el elegido queda como parada antes del destino.
     */
    FIND_PLACES,
    /** Quitar la parada de la ruta («ya no quiero parar», «quita la parada»). */
    STOP_REMOVE,
    /** Cuánto falta para la parada o cuál es. */
    STOP_INFO,
    /** Seguir el viaje después de parar («seguimos», «ya comí, vámonos»). */
    RESUME_TRIP,
    /** Próximo peaje de la ruta: distancia y tarifa de HERE para el camión. */
    TOLLS,
    REPEAT,
    HOURS,
    LOAD_INFO,
    HELP,
    /** Respuesta hablada sin acción (pregunta general de conducción). */
    CHAT,
    UNKNOWN
}
