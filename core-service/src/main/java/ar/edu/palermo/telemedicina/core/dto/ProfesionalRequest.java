package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Alta o modificación de un profesional.
 * La contraseña es obligatoria solo en el alta; en la modificación se ignora.
 */
public record ProfesionalRequest(
        @NotBlank @Email String email,
        String password,
        @NotBlank String apellido,
        @NotBlank String nombre,
        @NotBlank String matricula,
        @NotBlank String especialidad) {
}
