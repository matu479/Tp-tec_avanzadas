package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.NivelAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Alerta generada por notificaciones-service que el core tiene que registrar (S3). */
public record AlertaRequest(@NotNull Integer idPaciente, @NotNull NivelAlerta nivel, @NotBlank String mensaje,
                            LocalDateTime fechaHora) {
}
