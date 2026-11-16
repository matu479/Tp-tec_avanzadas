package ar.edu.palermo.telemedicina.notificaciones.model;

import ar.edu.palermo.telemedicina.notificaciones.dto.EventoRiesgo;
import java.time.LocalDateTime;

/**
 * Alerta médica del lado de notificaciones (clase simple, no es entidad; S7).
 * Se crea a partir del evento de riesgo, se genera, se registra en el core y se envía.
 */
public class AlertaMedica {

    private final EventoRiesgo evento;
    private Integer idAlerta;
    private String nivel;
    private String mensaje;
    private String estado;
    private LocalDateTime fechaHora;
    private Integer idPaciente;
    private Integer idProfesional;
    private String emailDestinatario;
    private String nombrePaciente;
    private ServicioNotificacion servicioNotificacion;

    public AlertaMedica(EventoRiesgo evento, ServicioNotificacion servicioNotificacion) {
        this.evento = evento;
        this.servicioNotificacion = servicioNotificacion;
    }

    /** Método del UML: completa mensaje, nivel, fecha y estado GENERADA a partir del evento. */
    public void generar() {
        this.idPaciente = evento.idPaciente();
        this.nivel = evento.nivel();
        this.fechaHora = evento.fechaHora() != null ? evento.fechaHora() : LocalDateTime.now();
        this.mensaje = String.format("%s fuera de rango: %s %s (dispositivo %s)", evento.tipo(),
                evento.valor(), evento.unidad() != null ? evento.unidad() : "", evento.idDispositivo());
        this.estado = "GENERADA";
    }

    /** Guarda los datos que devolvió el core al registrar la alerta (o los de la guardia). */
    public void asignarDestino(Integer idAlerta, Integer idProfesional, String email, String nombrePaciente) {
        this.idAlerta = idAlerta;
        this.idProfesional = idProfesional;
        this.emailDestinatario = email;
        this.nombrePaciente = nombrePaciente;
    }

    /** Método del UML: delega el envío en el ServicioNotificacion (polimorfismo). */
    public void enviarAlerta() {
        servicioNotificacion.enviarAlerta(this);
    }

    public void setServicioNotificacion(ServicioNotificacion servicioNotificacion) {
        this.servicioNotificacion = servicioNotificacion;
    }

    public EventoRiesgo getEvento() {
        return evento;
    }

    public Integer getIdAlerta() {
        return idAlerta;
    }

    public String getNivel() {
        return nivel;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public Integer getIdProfesional() {
        return idProfesional;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }
}
