package ar.edu.palermo.telemedicina.metricas.exception;

/** Métrica con un valor físicamente imposible. Se responde 422. */
public class DatoInvalidoException extends RuntimeException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
