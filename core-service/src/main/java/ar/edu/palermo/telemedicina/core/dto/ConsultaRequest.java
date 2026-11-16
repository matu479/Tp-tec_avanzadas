package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.NotNull;

/** Registro de una consulta al atender una cita. El diagnóstico es opcional. */
public record ConsultaRequest(@NotNull Integer idCita, String observaciones, String indicaciones,
                              String diagnostico) {
}
