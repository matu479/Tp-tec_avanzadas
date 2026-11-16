package ar.edu.palermo.telemedicina.notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** EVENTO DE RIESGO que envía metricas-service cuando una métrica supera el umbral. */
public record EventoRiesgo(@NotNull Integer idPaciente, Integer idDispositivo, @NotBlank String tipo,
                           @NotNull Double valor, String unidad, @NotBlank String nivel,
                           LocalDateTime fechaHora) {
}
