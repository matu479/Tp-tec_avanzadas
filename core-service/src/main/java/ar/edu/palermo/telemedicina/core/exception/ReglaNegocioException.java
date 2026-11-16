package ar.edu.palermo.telemedicina.core.exception;

/**
 * Se lanza cuando una operación viola una regla de negocio (ej.: cancelar una cita ya cancelada). Se responde 409.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
