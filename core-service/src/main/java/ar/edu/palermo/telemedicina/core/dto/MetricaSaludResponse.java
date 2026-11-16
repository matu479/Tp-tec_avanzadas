package ar.edu.palermo.telemedicina.core.dto;

import java.time.LocalDateTime;

/**
 * Métrica de salud tal como la devuelve metricas-service (el core no la guarda: vive en MongoDB).
 * Es lo que el UML llama List&lt;MetricaSalud&gt; en Paciente.consultarMetricas().
 */
public record MetricaSaludResponse(String idMetrica, Integer idPaciente, Integer idDispositivo, String tipo,
                                   double valor, String unidad, LocalDateTime fechaHora, boolean superaUmbral) {
}
