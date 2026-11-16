package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.dto.AlertaResponse;
import ar.edu.palermo.telemedicina.core.dto.ExpedienteResponse;
import ar.edu.palermo.telemedicina.core.dto.ProfesionalRequest;
import ar.edu.palermo.telemedicina.core.dto.ProfesionalResponse;
import ar.edu.palermo.telemedicina.core.exception.DatoInvalidoException;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.exception.ReglaNegocioException;
import ar.edu.palermo.telemedicina.core.model.Paciente;
import ar.edu.palermo.telemedicina.core.model.Profesional;
import ar.edu.palermo.telemedicina.core.repository.CitaRepository;
import ar.edu.palermo.telemedicina.core.repository.ProfesionalRepository;
import ar.edu.palermo.telemedicina.core.repository.UsuarioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. ABM de profesionales y consulta del historial de un paciente.
 */
@Service
public class ProfesionalService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfesionalService.class);

    private final ProfesionalRepository profesionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final PacienteService pacienteService;

    public ProfesionalService(ProfesionalRepository profesionalRepository, UsuarioRepository usuarioRepository,
                              CitaRepository citaRepository, PacienteService pacienteService) {
        this.profesionalRepository = profesionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional
    public ProfesionalResponse crear(ProfesionalRequest pedido) {
        validarEmailLibre(pedido.email());
        if (pedido.password() == null || pedido.password().isBlank()) {
            throw new DatoInvalidoException("La contraseña es obligatoria en el alta");
        }
        Profesional profesional = new Profesional(pedido.email(), pedido.password(), pedido.apellido(),
                pedido.nombre(), pedido.matricula(), pedido.especialidad());
        Profesional guardado = profesionalRepository.save(profesional);
        LOG.info("Profesional {} creado", guardado.getIdUsuario());
        return ProfesionalResponse.desde(guardado);
    }

    @Transactional(readOnly = true)
    public List<ProfesionalResponse> listar() {
        return profesionalRepository.findAll().stream().map(ProfesionalResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public ProfesionalResponse obtener(Integer idProfesional) {
        return ProfesionalResponse.desde(buscarProfesional(idProfesional));
    }

    @Transactional
    public ProfesionalResponse actualizar(Integer idProfesional, ProfesionalRequest pedido) {
        Profesional profesional = buscarProfesional(idProfesional);
        if (!profesional.getEmail().equals(pedido.email())) {
            validarEmailLibre(pedido.email());
        }
        profesional.setEmail(pedido.email());
        profesional.setNombre(pedido.nombre());
        profesional.setApellido(pedido.apellido());
        profesional.actualizarDatos(pedido.matricula(), pedido.especialidad());
        LOG.info("Profesional {} actualizado", idProfesional);
        return ProfesionalResponse.desde(profesional);
    }

    /** Solo se borra un profesional sin citas (si no, 409). */
    @Transactional
    public void eliminar(Integer idProfesional) {
        Profesional profesional = buscarProfesional(idProfesional);
        if (citaRepository.existsByProfesional_IdUsuario(idProfesional)) {
            throw new ReglaNegocioException("No se puede eliminar el profesional: tiene citas");
        }
        profesionalRepository.delete(profesional);
        LOG.info("Profesional {} eliminado", idProfesional);
    }

    /** Método del UML consultarHistorial(paciente): devuelve el expediente con sus consultas. */
    @Transactional(readOnly = true)
    public ExpedienteResponse consultarHistorial(Integer idProfesional, Integer idPaciente) {
        Profesional profesional = buscarProfesional(idProfesional);
        Paciente paciente = pacienteService.buscarPaciente(idPaciente);
        return ExpedienteResponse.desde(profesional.consultarHistorial(paciente));
    }

    /** Método del UML revisarAlertas(): alertas no atendidas asignadas al profesional (S5). */
    @Transactional(readOnly = true)
    public List<AlertaResponse> revisarAlertas(Integer idProfesional) {
        return buscarProfesional(idProfesional).revisarAlertas().stream().map(AlertaResponse::desde).toList();
    }

    /**
     * Cambia el email de un profesional de prueba (se usa al arrancar, ver EmailsProfesionalesConfig).
     * Si el email viene vacío o el profesional no existe, no hace nada.
     */
    @Transactional
    public void cambiarEmailDePrueba(Integer idProfesional, String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        profesionalRepository.findById(idProfesional).ifPresent(profesional -> {
            profesional.setEmail(email);
            LOG.info("Email del profesional {} configurado por variable de entorno", idProfesional);
        });
    }

    /** Busca la entidad o lanza 404. Lo usan también otros services. */
    @Transactional(readOnly = true)
    public Profesional buscarProfesional(Integer idProfesional) {
        return profesionalRepository.findById(idProfesional)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el profesional " + idProfesional));
    }

    private void validarEmailLibre(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + email);
        }
    }
}
