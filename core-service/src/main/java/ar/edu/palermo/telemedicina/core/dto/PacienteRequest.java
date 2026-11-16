package ar.edu.palermo.telemedicina.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

/**
 * Alta o modificación de un paciente.
 * La contraseña es obligatoria solo en el alta; en la modificación se ignora.
 */
public record PacienteRequest(
        @NotBlank @Email String email,
        String password,
        @NotBlank String apellido,
        @NotBlank String nombre,
        @NotBlank String dni,
        @NotNull @Past LocalDate fechaNacimiento) {
}
