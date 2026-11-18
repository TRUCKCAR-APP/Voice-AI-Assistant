package escuelaing.edu.co.truckcar.voice_ai_assistant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Orden del conductor ya transcrita en el teléfono (el audio nunca sale del dispositivo).
 */
public record InterpretRequest(@NotBlank @Size(max = 300) String text, @Valid DriverContext context) {
}
