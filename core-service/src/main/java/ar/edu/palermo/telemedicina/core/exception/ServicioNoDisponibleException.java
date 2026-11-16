package ar.edu.palermo.telemedicina.core.exception;

/**
 * Se lanza cuando otro microservicio no responde. Se responde 503.
 */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
