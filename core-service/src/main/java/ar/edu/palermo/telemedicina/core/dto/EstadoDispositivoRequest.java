package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.EstadoDispositivo;
import jakarta.validation.constraints.NotNull;

/** Nuevo estado de un dispositivo (ACTIVO / INACTIVO). */
public record EstadoDispositivoRequest(@NotNull EstadoDispositivo estado) {
}
