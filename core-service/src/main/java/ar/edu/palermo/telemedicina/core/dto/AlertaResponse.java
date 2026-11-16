package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.AlertaMedica;
import ar.edu.palermo.telemedicina.core.model.Profesional;
import java.time.LocalDateTime;

/**
 * Alerta registrada. Incluye el nombre del profesional asignado (para mostrarlo en el front) y su email
 * (lo que notificaciones-service necesita para mandar el mail). Si no hay profesional, ambos son null.
 */
public record AlertaResponse(Integer id, Integer idPaciente, String nombrePaciente, Integer idProfesional,
                             String nombreProfesional, String emailProfesional, String nivel, String mensaje,
                             String estado, LocalDateTime fechaHora) {

    public static AlertaResponse desde(AlertaMedica alerta) {
        Profesional profesional = alerta.getProfesional();
        return new AlertaResponse(alerta.getIdAlerta(), alerta.getPaciente().getIdUsuario(),
                alerta.getPaciente().getNombre() + " " + alerta.getPaciente().getApellido(),
                profesional != null ? profesional.getIdUsuario() : null,
                profesional != null ? profesional.getNombre() + " " + profesional.getApellido() : null,
                profesional != null ? profesional.getEmail() : null,
                alerta.getNivel().name(), alerta.getMensaje(), alerta.getEstado().name(), alerta.getFechaHora());
    }
}
