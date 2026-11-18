package escuelaing.edu.co.truckcar.voice_ai_assistant.dto;

import jakarta.validation.constraints.Size;

/** Lo que el Copiloto sabe del viaje en el momento de la orden; ayuda al modelo a desambiguar. */
public record DriverContext(boolean hasLoad, boolean navigating, @Size(max = 40) String loadStatus) {

    public static final DriverContext NONE = new DriverContext(false, false, null);
}
