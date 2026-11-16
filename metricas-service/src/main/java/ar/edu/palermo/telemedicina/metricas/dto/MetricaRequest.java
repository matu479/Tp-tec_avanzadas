package ar.edu.palermo.telemedicina.metricas.dto;

import ar.edu.palermo.telemedicina.metricas.model.TipoMetrica;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Métrica que envía un dispositivo IoT. unidad y fechaHora son opcionales. */
public record MetricaRequest(@NotNull Integer idDispositivo, @NotNull Integer idPaciente,
                             @NotNull TipoMetrica tipo, @NotNull Double valor,
                             String unidad, LocalDateTime fechaHora) {
}
