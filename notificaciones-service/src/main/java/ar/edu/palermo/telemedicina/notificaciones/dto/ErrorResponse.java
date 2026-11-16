package ar.edu.palermo.telemedicina.notificaciones.dto;

import java.time.LocalDateTime;

/** Formato único de error: {timestamp, status, error, mensaje, path}. */
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String mensaje, String path) {
}
