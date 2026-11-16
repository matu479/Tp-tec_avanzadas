package ar.edu.palermo.telemedicina.metricas.service;

import ar.edu.palermo.telemedicina.metricas.client.NotificacionesClient;
import ar.edu.palermo.telemedicina.metricas.dto.EventoRiesgo;
import ar.edu.palermo.telemedicina.metricas.dto.MetricaRequest;
import ar.edu.palermo.telemedicina.metricas.dto.MetricaResponse;
import ar.edu.palermo.telemedicina.metricas.exception.DatoInvalidoException;
import ar.edu.palermo.telemedicina.metricas.model.MetricaSalud;
import ar.edu.palermo.telemedicina.metricas.model.TipoMetrica;
import ar.edu.palermo.telemedicina.metricas.repository.MetricaSaludRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * CAPA DE NEGOCIO. Recepción de datos IoT, procesamiento, evaluación de umbrales y consulta.
 */
@Service
public class MetricaService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricaService.class);

    private final MetricaSaludRepository metricaRepository;
    private final NotificacionesClient notificacionesClient;

    public MetricaService(MetricaSaludRepository metricaRepository, NotificacionesClient notificacionesClient) {
        this.metricaRepository = metricaRepository;
        this.notificacionesClient = notificacionesClient;
    }

    /**
     * Valida la métrica (si es imposible → 422), la guarda en Mongo y, si supera el umbral,
     * dispara el evento de riesgo de forma asíncrona (no se espera la respuesta).
     */
    public MetricaResponse registrar(MetricaRequest pedido) {
        MetricaSalud metrica = new MetricaSalud(pedido.idPaciente(), pedido.idDispositivo(), pedido.tipo(),
                pedido.valor(), pedido.unidad(), pedido.fechaHora());
        if (!metrica.validar()) {
            LOG.warn("Métrica descartada por valor imposible: {}={} (dispositivo {})", metrica.getTipo(),
                    metrica.getValor(), metrica.getIdDispositivo());
            throw new DatoInvalidoException("El valor " + metrica.getValor() + " no es posible para "
                    + metrica.getTipo());
        }
        MetricaSalud guardada = metricaRepository.save(metrica);
        if (guardada.superaUmbral()) {
            LOG.info("Métrica fuera de rango: paciente {} {}={} → evento de riesgo {}", guardada.getIdPaciente(),
                    guardada.getTipo(), guardada.getValor(), guardada.calcularNivel());
            notificacionesClient.enviarEventoRiesgo(EventoRiesgo.desde(guardada));
        }
        return MetricaResponse.desde(guardada);
    }

    /** Consulta de métricas de un paciente. Sin fechas: las últimas 24 horas. */
    public List<MetricaResponse> consultar(Integer idPaciente, TipoMetrica tipo, LocalDateTime desde,
                                           LocalDateTime hasta) {
        LocalDateTime fin = hasta != null ? hasta : LocalDateTime.now();
        LocalDateTime inicio = desde != null ? desde : fin.minusHours(24);
        List<MetricaSalud> metricas = tipo == null
                ? metricaRepository.findByIdPacienteAndFechaHoraBetweenOrderByFechaHoraDesc(idPaciente, inicio, fin)
                : metricaRepository.findByIdPacienteAndTipoAndFechaHoraBetweenOrderByFechaHoraDesc(
                        idPaciente, tipo, inicio, fin);
        return metricas.stream().map(MetricaResponse::desde).toList();
    }
}
