package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.dto.ConsultaRequest;
import ar.edu.palermo.telemedicina.core.dto.ConsultaResponse;
import ar.edu.palermo.telemedicina.core.dto.DiagnosticoResponse;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.model.Cita;
import ar.edu.palermo.telemedicina.core.model.ConsultaMedica;
import ar.edu.palermo.telemedicina.core.model.ExpedienteClinico;
import ar.edu.palermo.telemedicina.core.repository.ConsultaMedicaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. Registrar una consulta al atender una cita y completar diagnóstico, indicaciones
 * y observaciones.
 */
@Service
public class ConsultaService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultaService.class);

    private final ConsultaMedicaRepository consultaRepository;
    private final CitaService citaService;

    public ConsultaService(ConsultaMedicaRepository consultaRepository, CitaService citaService) {
        this.consultaRepository = consultaRepository;
        this.citaService = citaService;
    }

    /**
     * Método del UML Profesional.registrarConsulta(): la cita tiene que estar RESERVADA y pasa a ATENDIDA;
     * la consulta se agrega al expediente del paciente.
     */
    @Transactional
    public ConsultaResponse registrar(ConsultaRequest pedido) {
        Cita cita = citaService.buscarCita(pedido.idCita());
        cita.marcarAtendida();
        ExpedienteClinico expediente = cita.getPaciente().consultarExpediente();
        ConsultaMedica consulta = new ConsultaMedica(cita, expediente, pedido.observaciones(),
                pedido.indicaciones());
        if (pedido.diagnostico() != null && !pedido.diagnostico().isBlank()) {
            consulta.registrarDiagnostico(pedido.diagnostico());
        }
        cita.getProfesional().registrarConsulta(consulta);
        expediente.agregarConsulta(consulta);
        ConsultaMedica guardada = consultaRepository.save(consulta);
        LOG.info("Consulta {} registrada para la cita {}", guardada.getIdConsulta(), cita.getIdCita());
        return ConsultaResponse.desde(guardada);
    }

    @Transactional(readOnly = true)
    public ConsultaResponse obtener(Integer idConsulta) {
        return ConsultaResponse.desde(buscarConsulta(idConsulta));
    }

    /** Método del UML registrarDiagnostico(String): lo crea o lo actualiza. Lo usan REST y GraphQL. */
    @Transactional
    public DiagnosticoResponse registrarDiagnostico(Integer idConsulta, String descripcion) {
        ConsultaMedica consulta = buscarConsulta(idConsulta);
        consulta.registrarDiagnostico(descripcion);
        consultaRepository.saveAndFlush(consulta);
        LOG.info("Diagnóstico registrado en la consulta {}", idConsulta);
        return DiagnosticoResponse.desde(consulta.getDiagnostico());
    }

    /** Método del UML agregarIndicacion(String). */
    @Transactional
    public ConsultaResponse agregarIndicacion(Integer idConsulta, String indicacion) {
        ConsultaMedica consulta = buscarConsulta(idConsulta);
        consulta.agregarIndicacion(indicacion);
        return ConsultaResponse.desde(consulta);
    }

    /** Método del UML agregarObservacion(String). */
    @Transactional
    public ConsultaResponse agregarObservacion(Integer idConsulta, String observacion) {
        ConsultaMedica consulta = buscarConsulta(idConsulta);
        consulta.agregarObservacion(observacion);
        return ConsultaResponse.desde(consulta);
    }

    private ConsultaMedica buscarConsulta(Integer idConsulta) {
        return consultaRepository.findById(idConsulta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la consulta " + idConsulta));
    }
}
