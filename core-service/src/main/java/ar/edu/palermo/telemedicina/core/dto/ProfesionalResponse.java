package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.Profesional;

/** Datos públicos de un profesional. Los nombres coinciden con el tipo GraphQL Profesional. */
public record ProfesionalResponse(Integer id, String email, String nombre, String apellido,
                                  String matricula, String especialidad) {

    public static ProfesionalResponse desde(Profesional profesional) {
        return new ProfesionalResponse(profesional.getIdUsuario(), profesional.getEmail(),
                profesional.getNombre(), profesional.getApellido(),
                profesional.getMatricula(), profesional.getEspecialidad());
    }
}
