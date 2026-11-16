package ar.edu.palermo.telemedicina.core.exception;

import ar.edu.palermo.telemedicina.core.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Componente transversal: traduce las excepciones a códigos HTTP con un formato único
 * {timestamp, status, error, mensaje, path}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({RecursoNoEncontradoException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> noEncontrado(Exception ex, HttpServletRequest pedido) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), pedido);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), pedido);
    }

    /** Por ejemplo, borrar algo que otra tabla todavía referencia. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex, HttpServletRequest pedido) {
        LOG.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "La operación viola una restricción de la base de datos", pedido);
    }

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<ErrorResponse> datoInvalido(DatoInvalidoException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), pedido);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> credenciales(CredencialesInvalidasException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.UNAUTHORIZED, ex.getMessage(), pedido);
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> noDisponible(ServicioNoDisponibleException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), pedido);
    }

    /** Bean Validation (@Valid): lista los campos con error. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest pedido) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return responder(HttpStatus.BAD_REQUEST, mensaje, pedido);
    }

    /** JSON mal formado, parámetros faltantes o de tipo incorrecto. */
    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> pedidoMalFormado(Exception ex, HttpServletRequest pedido) {
        return responder(HttpStatus.BAD_REQUEST, "El pedido está mal formado o tiene datos de tipo incorrecto",
                pedido);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodo(HttpRequestMethodNotSupportedException ex,
                                                HttpServletRequest pedido) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), pedido);
    }

    /** Cualquier otro error: 500 con mensaje genérico; el detalle va al log. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex, HttpServletRequest pedido) {
        LOG.error("Error inesperado en {}", pedido.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno", pedido);
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String mensaje, HttpServletRequest pedido) {
        ErrorResponse cuerpo = new ErrorResponse(LocalDateTime.now(), estado.value(), estado.getReasonPhrase(),
                mensaje, pedido.getRequestURI());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
