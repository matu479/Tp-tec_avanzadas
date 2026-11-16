package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.ExpedienteClinico;
import java.time.LocalDate;
import java.util.List;

/**
 * Expediente con TODO su historial de consultas.
 * En REST siempre viene completo (overfetching); en GraphQL se piden solo los campos necesarios.
 */
public record ExpedienteResponse(Integer id, Integer idPaciente, LocalDate fechaCreacion,
                                 String antecedentes, String observacionesGenerales,
                                 List<ConsultaResponse> consultas) {

    public static ExpedienteResponse desde(ExpedienteClinico expediente) {
        List<ConsultaResponse> consultas = expediente.obtenerHistorial().stream()
                .map(ConsultaResponse::desde)
                .toList();
        return new ExpedienteResponse(expediente.getIdExpediente(),
                expediente.getPaciente().getIdUsuario(), expediente.getFechaCreacion(),
                expediente.getAntecedentes(), expediente.getObservacionesGenerales(), consultas);
    }
}
