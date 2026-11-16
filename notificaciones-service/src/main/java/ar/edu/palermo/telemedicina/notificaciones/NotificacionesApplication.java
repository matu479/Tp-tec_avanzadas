package ar.edu.palermo.telemedicina.notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de notificaciones-service (notificaciones y alertas médicas).
 */
@SpringBootApplication
public class NotificacionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificacionesApplication.class, args);
    }
}
