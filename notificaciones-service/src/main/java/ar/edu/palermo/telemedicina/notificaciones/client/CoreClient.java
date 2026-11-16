package ar.edu.palermo.telemedicina.notificaciones.client;

import ar.edu.palermo.telemedicina.notificaciones.dto.AlertaParaRegistrar;
import ar.edu.palermo.telemedicina.notificaciones.dto.AlertaRegistrada;
import ar.edu.palermo.telemedicina.notificaciones.model.AlertaMedica;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * "Registro de alertas": le pide al core que guarde la alerta (POST /api/alertas).
 * Si el core no responde, devuelve vacío y se loguea ERROR (el email se manda igual).
 */
@Component
public class CoreClient {

    private static final Logger LOG = LoggerFactory.getLogger(CoreClient.class);

    private final RestClient restClient;

    public CoreClient(RestClient coreRestClient) {
        this.restClient = coreRestClient;
    }

    public Optional<AlertaRegistrada> registrarAlerta(AlertaMedica alerta) {
        try {
            AlertaRegistrada registrada = restClient.post().uri("/api/alertas")
                    .body(AlertaParaRegistrar.desde(alerta))
                    .retrieve()
                    .body(AlertaRegistrada.class);
            return Optional.ofNullable(registrada);
        } catch (RestClientException ex) {
            LOG.error("No se pudo registrar la alerta del paciente {} en el core", alerta.getIdPaciente(), ex);
            return Optional.empty();
        }
    }
}
