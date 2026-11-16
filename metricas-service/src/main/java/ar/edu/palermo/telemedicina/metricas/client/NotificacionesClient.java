package ar.edu.palermo.telemedicina.metricas.client;

import ar.edu.palermo.telemedicina.metricas.dto.EventoRiesgo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente REST hacia notificaciones-service. El envío es ASÍNCRONO (@Async): corre en otro hilo,
 * así quien registra la métrica no espera. Si notificaciones no responde, se loguea y listo
 * (la métrica ya quedó guardada).
 */
@Component
public class NotificacionesClient {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacionesClient.class);

    private final RestClient restClient;

    public NotificacionesClient(RestClient notificacionesRestClient) {
        this.restClient = notificacionesRestClient;
    }

    @Async
    public void enviarEventoRiesgo(EventoRiesgo evento) {
        try {
            restClient.post().uri("/api/eventos-riesgo").body(evento).retrieve().toBodilessEntity();
            LOG.info("Evento de riesgo enviado: paciente {} {}={} nivel {}", evento.idPaciente(),
                    evento.tipo(), evento.valor(), evento.nivel());
        } catch (RestClientException ex) {
            LOG.error("No se pudo enviar el evento de riesgo del paciente {} a notificaciones-service",
                    evento.idPaciente(), ex);
        }
    }
}
