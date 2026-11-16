package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.dto.AlertaRequest;
import ar.edu.palermo.telemedicina.core.dto.AlertaResponse;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.model.AlertaMedica;
import ar.edu.palermo.telemedicina.core.model.Cita;
import ar.edu.palermo.telemedicina.core.model.EstadoAlerta;
import ar.edu.palermo.telemedicina.core.model.Paciente;
import ar.edu.palermo.telemedicina.core.model.Profesional;
import ar.edu.palermo.telemedicina.core.repository.AlertaMedicaRepository;
import ar.edu.palermo.telemedicina.core.repository.CitaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. "Registro de alertas": notificaciones-service genera la alerta y el core la guarda (S3).
 */
@Service
public class AlertaService {

    private static final Logger LOG = LoggerFactory.getLogger(AlertaService.class);

    private final AlertaMedicaRepository alertaRepository;
    private final CitaRepository citaRepository;
    private final PacienteService pacienteService;

    public AlertaService(AlertaMedicaRepository alertaRepository, CitaRepository citaRepository,
                         PacienteService pacienteService) {
        this.alertaRepository = alertaRepository;
        this.citaRepository = citaRepository;
        this.pacienteService = pacienteService;
    }

    /** Registra la alerta y le asigna el profesional de la última cita del paciente (S12). */
    @Transactional
    public AlertaResponse registrar(AlertaRequest pedido) {
        Paciente paciente = pacienteService.buscarPaciente(pedido.idPaciente());
        Profesional profesional = citaRepository.findFirstByPaciente_IdUsuarioOrderByFechaHoraDesc(paciente.getIdUsuario())
                .map(Cita::getProfesional)
                .orElse(null);
        LocalDateTime fechaHora = pedido.fechaHora() != null ? pedido.fechaHora() : LocalDateTime.now();
        AlertaMedica guardada = alertaRepository.save(
                new AlertaMedica(paciente, profesional, pedido.nivel(), pedido.mensaje(), fechaHora));
        LOG.info("Alerta {} registrada: paciente {} nivel {} profesional {}", guardada.getIdAlerta(),
                paciente.getIdUsuario(), guardada.getNivel(),
                profesional != null ? profesional.getIdUsuario() : "ninguno (guardia)");
        return AlertaResponse.desde(guardada);
    }

    /** Listado con filtros opcionales por estado, paciente y profesional, de la más nueva a la más vieja. */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listar(EstadoAlerta estado, Integer idPaciente, Integer idProfesional) {
        return alertaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHora")).stream()
                .filter(alerta -> estado == null || alerta.getEstado() == estado)
                .filter(alerta -> idPaciente == null || alerta.getPaciente().getIdUsuario().equals(idPaciente))
                .filter(alerta -> idProfesional == null || esDelProfesional(alerta, idProfesional))
                .map(AlertaResponse::desde)
                .toList();
    }

    private static boolean esDelProfesional(AlertaMedica alerta, Integer idProfesional) {
        return alerta.getProfesional() != null && alerta.getProfesional().getIdUsuario().equals(idProfesional);
    }

    /** Método del UML marcarAtendida(): 409 si ya estaba atendida. */
    @Transactional
    public AlertaResponse atender(Integer idAlerta) {
        AlertaMedica alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la alerta " + idAlerta));
        alerta.marcarAtendida();
        LOG.info("Alerta {} atendida", idAlerta);
        return AlertaResponse.desde(alerta);
    }
}
