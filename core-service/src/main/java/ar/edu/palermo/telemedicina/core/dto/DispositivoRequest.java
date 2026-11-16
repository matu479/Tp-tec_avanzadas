package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Alta de un dispositivo IoT para un paciente. */
public record DispositivoRequest(@NotNull Integer idPaciente, @NotBlank String tipo) {
}
