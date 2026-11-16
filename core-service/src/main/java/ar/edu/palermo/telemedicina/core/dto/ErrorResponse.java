package ar.edu.palermo.telemedicina.core.dto;

import java.time.LocalDateTime;

/** Formato único de error de toda la API: {timestamp, status, error, mensaje, path}. */
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String mensaje, String path) {
}
