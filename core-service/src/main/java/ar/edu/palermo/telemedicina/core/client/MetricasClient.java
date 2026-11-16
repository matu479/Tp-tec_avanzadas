package ar.edu.palermo.telemedicina.core.client;

import ar.edu.palermo.telemedicina.core.dto.MetricaSaludResponse;
import ar.edu.palermo.telemedicina.core.exception.DatoInvalidoException;
import ar.edu.palermo.telemedicina.core.exception.ServicioNoDisponibleException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * CONSULTA DE MÉTRICAS (REST) hacia metricas-service, como en el Diagrama de Sistema.
 * Si el microservicio no responde → ServicioNoDisponibleException (503).
 */
@Component
public class MetricasClient {

    private static final Logger LOG = LoggerFactory.getLogger(MetricasClient.class);

    private final RestClient restClient;

    public MetricasClient(RestClient metricasRestClient) {
        this.restClient = metricasRestClient;
    }

    public List<MetricaSaludResponse> consultarMetricas(Integer idPaciente, String tipo,
                                                        LocalDateTime desde, LocalDateTime hasta) {
        try {
            return restClient.get()
                    .uri(uri -> uri.path("/api/metricas")
                            .queryParam("idPaciente", idPaciente)
                            .queryParamIfPresent("tipo", Optional.ofNullable(tipo))
                            .queryParamIfPresent("desde", Optional.ofNullable(desde))
                            .queryParamIfPresent("hasta", Optional.ofNullable(hasta))
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MetricaSaludResponse>>() { });
        } catch (HttpClientErrorException ex) {
            throw new DatoInvalidoException("metricas-service rechazó la consulta: " + ex.getStatusText());
        } catch (RestClientException ex) {
            LOG.error("metricas-service no responde", ex);
            throw new ServicioNoDisponibleException("El servicio de métricas no está disponible");
        }
    }
}
