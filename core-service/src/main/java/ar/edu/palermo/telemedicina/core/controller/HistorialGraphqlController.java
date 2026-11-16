package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.CitaResponse;
import ar.edu.palermo.telemedicina.core.dto.ConsultaResponse;
import ar.edu.palermo.telemedicina.core.dto.DiagnosticoResponse;
import ar.edu.palermo.telemedicina.core.dto.ExpedienteResponse;
import ar.edu.palermo.telemedicina.core.dto.PacienteResponse;
import ar.edu.palermo.telemedicina.core.dto.ProfesionalResponse;
import ar.edu.palermo.telemedicina.core.service.ConsultaService;
import ar.edu.palermo.telemedicina.core.service.PacienteService;
import ar.edu.palermo.telemedicina.core.service.ProfesionalService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * CAPA DE PRESENTACIÓN (GraphQL). Resolvers del esquema {@code graphql/schema.graphqls}.
 * Usan LOS MISMOS services que REST: GraphQL es otra "puerta de entrada", no otra lógica.
 * GraphQL devuelve solo los campos que pide el cliente.
 */
@Controller
public class HistorialGraphqlController {

    private final PacienteService pacienteService;
    private final ProfesionalService profesionalService;
    private final ConsultaService consultaService;

    public HistorialGraphqlController(PacienteService pacienteService, ProfesionalService profesionalService,
                                      ConsultaService consultaService) {
        this.pacienteService = pacienteService;
        this.profesionalService = profesionalService;
        this.consultaService = consultaService;
    }

    /** query { paciente(id: 4) { ... } } */
    @QueryMapping
    public PacienteResponse paciente(@Argument Integer id) {
        return pacienteService.obtener(id);
    }

    /** query { historialClinico(idPaciente: 4) { fechaConsulta diagnostico { descripcion } } } */
    @QueryMapping
    public List<ConsultaResponse> historialClinico(@Argument Integer idPaciente) {
        return pacienteService.consultarExpediente(idPaciente).consultas();
    }

    /** query { profesional(id: 3) { ... } } */
    @QueryMapping
    public ProfesionalResponse profesional(@Argument Integer id) {
        return profesionalService.obtener(id);
    }

    /** mutation { registrarDiagnostico(idConsulta: 1, descripcion: "...") { id descripcion } } */
    @MutationMapping
    public DiagnosticoResponse registrarDiagnostico(@Argument Integer idConsulta, @Argument String descripcion) {
        return consultaService.registrarDiagnostico(idConsulta, descripcion);
    }

    /** Campo Paciente.expediente: solo se consulta a la base si el cliente lo pide. */
    @SchemaMapping(typeName = "Paciente", field = "expediente")
    public ExpedienteResponse expediente(PacienteResponse paciente) {
        return pacienteService.consultarExpediente(paciente.id());
    }

    /** Campo Paciente.citas: solo se consulta a la base si el cliente lo pide. */
    @SchemaMapping(typeName = "Paciente", field = "citas")
    public List<CitaResponse> citas(PacienteResponse paciente) {
        return pacienteService.consultarCitas(paciente.id());
    }
}
