package ar.edu.palermo.telemedicina.notificaciones.exception;

import ar.edu.palermo.telemedicina.notificaciones.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Componente transversal: excepciones → códigos HTTP con formato único. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest pedido) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return responder(HttpStatus.BAD_REQUEST, mensaje, pedido);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> malFormado(HttpMessageNotReadableException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.BAD_REQUEST, "El pedido está mal formado", pedido);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(NoResourceFoundException ex, HttpServletRequest pedido) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), pedido);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex, HttpServletRequest pedido) {
        LOG.error("Error inesperado en {}", pedido.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno", pedido);
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String mensaje, HttpServletRequest pedido) {
        return ResponseEntity.status(estado).body(new ErrorResponse(LocalDateTime.now(), estado.value(),
                estado.getReasonPhrase(), mensaje, pedido.getRequestURI()));
    }
}
