package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Pedido de reserva de una cita. */
public record CitaRequest(@NotNull Integer idPaciente, @NotNull Integer idProfesional,
                          @NotNull LocalDateTime fechaHora, String motivo) {
}
