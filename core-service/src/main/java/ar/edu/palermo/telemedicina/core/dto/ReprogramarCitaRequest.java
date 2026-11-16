package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Nueva fecha y hora para reprogramar una cita. */
public record ReprogramarCitaRequest(@NotNull LocalDateTime fechaHora) {
}
