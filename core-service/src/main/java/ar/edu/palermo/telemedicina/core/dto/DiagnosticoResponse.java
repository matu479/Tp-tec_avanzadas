package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.Diagnostico;
import java.time.LocalDate;

/** Datos de un diagnóstico. Coincide con el tipo GraphQL Diagnostico. */
public record DiagnosticoResponse(Integer id, String descripcion, LocalDate fecha) {

    /** Devuelve null si la consulta no tiene diagnóstico (relación 0..1, S2). */
    public static DiagnosticoResponse desde(Diagnostico diagnostico) {
        if (diagnostico == null) {
            return null;
        }
        return new DiagnosticoResponse(diagnostico.getIdDiagnostico(), diagnostico.getDescripcion(),
                diagnostico.getFecha());
    }
}
