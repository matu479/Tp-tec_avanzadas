package ar.edu.palermo.telemedicina.core.exception;

/**
 * Se lanza cuando un recurso pedido no existe. Se responde 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
