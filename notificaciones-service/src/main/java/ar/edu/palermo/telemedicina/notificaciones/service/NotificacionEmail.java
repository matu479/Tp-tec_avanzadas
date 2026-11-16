package ar.edu.palermo.telemedicina.notificaciones.service;

import ar.edu.palermo.telemedicina.notificaciones.dto.EventoRiesgo;
import ar.edu.palermo.telemedicina.notificaciones.model.AlertaMedica;
import ar.edu.palermo.telemedicina.notificaciones.model.ServicioNotificacion;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Implementación de ServicioNotificacion por EMAIL (Spring Mail → MailHog en Docker).
 * Es la "NOTIFICACIÓN MÉDICA" al profesional del Diagrama de Sistema.
 */
@Component
public class NotificacionEmail implements ServicioNotificacion {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacionEmail.class);
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final JavaMailSender mailSender;
    private final String remitente;

    public NotificacionEmail(JavaMailSender mailSender,
                             @Value("${notificaciones.email-remitente}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviarAlerta(AlertaMedica alerta) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper ayudante = new MimeMessageHelper(mensaje, "UTF-8");
            ayudante.setFrom(remitente);
            ayudante.setTo(alerta.getEmailDestinatario());
            ayudante.setSubject("[ALERTA " + alerta.getNivel() + "] " + alerta.getNombrePaciente());
            ayudante.setText(armarHtml(alerta), true);
            mailSender.send(mensaje);
            LOG.info("Email de alerta enviado a {} (paciente {})", alerta.getEmailDestinatario(),
                    alerta.getIdPaciente());
        } catch (MessagingException | MailException ex) {
            LOG.error("No se pudo enviar el email de alerta a {}", alerta.getEmailDestinatario(), ex);
        }
    }

    /** HTML simple con paciente, tipo de métrica, valor, nivel y hora. */
    private static String armarHtml(AlertaMedica alerta) {
        EventoRiesgo evento = alerta.getEvento();
        String unidad = evento.unidad() != null ? evento.unidad() : "";
        return "<h2>Alerta médica " + alerta.getNivel() + "</h2>"
                + "<table border='1' cellpadding='6' style='border-collapse:collapse'>"
                + fila("Paciente", alerta.getNombrePaciente())
                + fila("Tipo de métrica", evento.tipo())
                + fila("Valor", evento.valor() + " " + unidad)
                + fila("Nivel", alerta.getNivel())
                + fila("Hora", alerta.getFechaHora().format(FORMATO))
                + "</table><p>" + alerta.getMensaje() + "</p>"
                + "<p><small>Sistema de Telemedicina - TP Universidad de Palermo</small></p>";
    }

    private static String fila(String titulo, String valor) {
        return "<tr><th align='left'>" + titulo + "</th><td>" + valor + "</td></tr>";
    }
}
