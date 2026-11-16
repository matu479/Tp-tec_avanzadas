package ar.edu.palermo.telemedicina.notificaciones.model;

/**
 * Interfaz del UML. Define CÓMO se avisa una alerta sin atarse a un medio concreto.
 * Hoy la implementa NotificacionEmail; mañana podría haber SMS o push sin tocar AlertaMedica (S5).
 */
public interface ServicioNotificacion {

    void enviarAlerta(AlertaMedica alerta);
}
