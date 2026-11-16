package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.NotBlank;

/** Un texto simple: diagnóstico, indicación u observación. */
public record TextoRequest(@NotBlank String texto) {
}
