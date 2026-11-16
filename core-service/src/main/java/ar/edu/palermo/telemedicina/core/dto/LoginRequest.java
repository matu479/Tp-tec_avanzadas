package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Datos del login. */
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
}
