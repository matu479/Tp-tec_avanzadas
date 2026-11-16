package ar.edu.palermo.telemedicina.metricas.dto;

import ar.edu.palermo.telemedicina.metricas.model.MetricaSalud;
import java.time.LocalDateTime;

/** EVENTO DE RIESGO que se envía a notificaciones-service cuando una métrica supera el umbral. */
public record EventoRiesgo(Integer idPaciente, Integer idDispositivo, String tipo, double valor,
                           String unidad, String nivel, LocalDateTime fechaHora) {

    public static EventoRiesgo desde(MetricaSalud metrica) {
        return new EventoRiesgo(metrica.getIdPaciente(), metrica.getIdDispositivo(), metrica.getTipo().name(),
                metrica.getValor(), metrica.getUnidad(), metrica.calcularNivel().name(), metrica.getFechaHora());
    }
}
