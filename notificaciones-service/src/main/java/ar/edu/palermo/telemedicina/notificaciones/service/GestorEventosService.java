package ar.edu.palermo.telemedicina.notificaciones.service;

import ar.edu.palermo.telemedicina.notificaciones.client.CoreClient;
import ar.edu.palermo.telemedicina.notificaciones.dto.AlertaRegistrada;
import ar.edu.palermo.telemedicina.notificaciones.dto.EventoRiesgo;
import ar.edu.palermo.telemedicina.notificaciones.model.AlertaMedica;
import ar.edu.palermo.telemedicina.notificaciones.model.ServicioNotificacion;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * CAPA DE NEGOCIO. "Gestión de eventos": por cada evento de riesgo genera la alerta,
 * la registra en el core y la envía por email. Corre en segundo plano (@Async).
 */
@Service
public class GestorEventosService {

    private static final Logger LOG = LoggerFactory.getLogger(GestorEventosService.class);

    private final CoreClient coreClient;
    private final ServicioNotificacion servicioNotificacion;
    private final String emailGuardia;

    /** Recibe la INTERFAZ ServicioNotificacion: Spring inyecta NotificacionEmail. */
    public GestorEventosService(CoreClient coreClient, ServicioNotificacion servicioNotificacion,
                                @Value("${notificaciones.email-guardia}") String emailGuardia) {
        this.coreClient = coreClient;
        this.servicioNotificacion = servicioNotificacion;
        this.emailGuardia = emailGuardia;
    }

    @Async
    public void procesar(EventoRiesgo evento) {
        AlertaMedica alerta = new AlertaMedica(evento, servicioNotificacion);
        alerta.generar();
        LOG.info("Alerta {} generada para el paciente {}: {}", alerta.getNivel(), alerta.getIdPaciente(),
                alerta.getMensaje());
        Optional<AlertaRegistrada> registrada = coreClient.registrarAlerta(alerta);
        asignarDestino(alerta, registrada);
        alerta.enviarAlerta();
    }

    /** Si el core asignó un profesional con email, va a él; si no, a la guardia (S12). */
    private void asignarDestino(AlertaMedica alerta, Optional<AlertaRegistrada> registrada) {
        String nombrePorDefecto = "Paciente #" + alerta.getIdPaciente();
        if (registrada.isEmpty()) {
            alerta.asignarDestino(null, null, emailGuardia, nombrePorDefecto);
            return;
        }
        AlertaRegistrada datos = registrada.get();
        String email = datos.emailProfesional() != null ? datos.emailProfesional() : emailGuardia;
        String nombre = datos.nombrePaciente() != null ? datos.nombrePaciente() : nombrePorDefecto;
        alerta.asignarDestino(datos.id(), datos.idProfesional(), email, nombre);
    }
}
