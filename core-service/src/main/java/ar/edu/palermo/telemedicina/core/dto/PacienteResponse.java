package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.Paciente;
import java.time.LocalDate;

/** Datos públicos de un paciente. Los nombres de campo coinciden con el tipo GraphQL Paciente. */
public record PacienteResponse(Integer id, String email, String nombre, String apellido,
                               String dni, LocalDate fechaNacimiento) {

    public static PacienteResponse desde(Paciente paciente) {
        return new PacienteResponse(paciente.getIdUsuario(), paciente.getEmail(), paciente.getNombre(),
                paciente.getApellido(), paciente.getDni(), paciente.getFechaNacimiento());
    }
}
