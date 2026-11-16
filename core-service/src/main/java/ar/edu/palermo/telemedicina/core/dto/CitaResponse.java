package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.Cita;
import java.time.LocalDateTime;

/** Datos de una cita (incluye el nombre del paciente para la agenda del profesional). Los nombres coinciden con el tipo GraphQL Cita. */
public record CitaResponse(Integer id, LocalDateTime fechaHora, String estado, String motivo,
                           Integer idPaciente, String nombrePaciente, ProfesionalResponse profesional) {

    public static CitaResponse desde(Cita cita) {
        return new CitaResponse(cita.getIdCita(), cita.getFechaHora(), cita.getEstado().name(),
                cita.getMotivo(), cita.getPaciente().getIdUsuario(),
                cita.getPaciente().getNombre() + " " + cita.getPaciente().getApellido(),
                ProfesionalResponse.desde(cita.getProfesional()));
    }
}
