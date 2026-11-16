package ar.edu.palermo.telemedicina.core.exception;

/**
 * Se lanza cuando el email o la contraseña del login no coinciden. Se responde 401.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
