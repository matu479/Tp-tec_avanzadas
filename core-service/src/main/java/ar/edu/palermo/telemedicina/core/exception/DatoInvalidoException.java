package ar.edu.palermo.telemedicina.core.exception;

/**
 * Se lanza cuando un dato es válido en formato pero no tiene sentido (ej.: fecha pasada). Se responde 422.
 */
public class DatoInvalidoException extends RuntimeException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
