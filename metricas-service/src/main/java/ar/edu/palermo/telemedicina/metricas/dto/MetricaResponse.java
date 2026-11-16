package ar.edu.palermo.telemedicina.metricas.dto;

import ar.edu.palermo.telemedicina.metricas.model.MetricaSalud;
import java.time.LocalDateTime;

/** Métrica guardada. superaUmbral indica si se disparó un evento de riesgo. */
public record MetricaResponse(String idMetrica, Integer idPaciente, Integer idDispositivo, String tipo,
                              double valor, String unidad, LocalDateTime fechaHora, boolean superaUmbral) {

    public static MetricaResponse desde(MetricaSalud metrica) {
        return new MetricaResponse(metrica.getIdMetrica(), metrica.getIdPaciente(), metrica.getIdDispositivo(),
                metrica.getTipo().name(), metrica.getValor(), metrica.getUnidad(), metrica.getFechaHora(),
                metrica.superaUmbral());
    }
}
