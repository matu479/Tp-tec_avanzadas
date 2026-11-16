package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.ConsultaMedica;
import java.time.LocalDateTime;

/** Datos de una consulta médica. Coincide con el tipo GraphQL ConsultaMedica. */
public record ConsultaResponse(Integer id, Integer idCita, LocalDateTime fechaConsulta,
                               String observaciones, String indicaciones,
                               ProfesionalResponse profesional, DiagnosticoResponse diagnostico) {

    public static ConsultaResponse desde(ConsultaMedica consulta) {
        return new ConsultaResponse(consulta.getIdConsulta(), consulta.getCita().getIdCita(),
                consulta.getFechaConsulta(), consulta.getObservaciones(), consulta.getIndicaciones(),
                ProfesionalResponse.desde(consulta.getProfesional()),
                DiagnosticoResponse.desde(consulta.getDiagnostico()));
    }
}
