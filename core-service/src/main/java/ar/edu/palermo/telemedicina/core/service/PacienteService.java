package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.client.MetricasClient;
import ar.edu.palermo.telemedicina.core.dto.CitaResponse;
import ar.edu.palermo.telemedicina.core.dto.ExpedienteResponse;
import ar.edu.palermo.telemedicina.core.dto.MetricaSaludResponse;
import ar.edu.palermo.telemedicina.core.dto.PacienteRequest;
import ar.edu.palermo.telemedicina.core.dto.PacienteResponse;
import ar.edu.palermo.telemedicina.core.exception.DatoInvalidoException;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.exception.ReglaNegocioException;
import ar.edu.palermo.telemedicina.core.model.ExpedienteClinico;
import ar.edu.palermo.telemedicina.core.model.Paciente;
import ar.edu.palermo.telemedicina.core.repository.CitaRepository;
import ar.edu.palermo.telemedicina.core.repository.DispositivoIotRepository;
import ar.edu.palermo.telemedicina.core.repository.PacienteRepository;
import ar.edu.palermo.telemedicina.core.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. ABM de pacientes y consultas sobre sus citas y su expediente.
 */
@Service
public class PacienteService {

    private static final Logger LOG = LoggerFactory.getLogger(PacienteService.class);

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final DispositivoIotRepository dispositivoRepository;
    private final MetricasClient metricasClient;

    public PacienteService(PacienteRepository pacienteRepository, UsuarioRepository usuarioRepository,
                           CitaRepository citaRepository, DispositivoIotRepository dispositivoRepository,
                           MetricasClient metricasClient) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.metricasClient = metricasClient;
    }

    /** Alta de paciente: también le crea su expediente clínico vacío. */
    @Transactional
    public PacienteResponse crear(PacienteRequest pedido) {
        validarEmailLibre(pedido.email());
        if (pedido.password() == null || pedido.password().isBlank()) {
            throw new DatoInvalidoException("La contraseña es obligatoria en el alta");
        }
        Paciente paciente = new Paciente(pedido.email(), pedido.password(), pedido.apellido(),
                pedido.nombre(), pedido.dni(), pedido.fechaNacimiento());
        paciente.asignarExpediente(new ExpedienteClinico(paciente));
        Paciente guardado = pacienteRepository.save(paciente);
        LOG.info("Paciente {} creado con su expediente", guardado.getIdUsuario());
        return PacienteResponse.desde(guardado);
    }

    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll().stream().map(PacienteResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public PacienteResponse obtener(Integer idPaciente) {
        return PacienteResponse.desde(buscarPaciente(idPaciente));
    }

    @Transactional
    public PacienteResponse actualizar(Integer idPaciente, PacienteRequest pedido) {
        Paciente paciente = buscarPaciente(idPaciente);
        if (!paciente.getEmail().equals(pedido.email())) {
            validarEmailLibre(pedido.email());
        }
        paciente.setEmail(pedido.email());
        paciente.setNombre(pedido.nombre());
        paciente.setApellido(pedido.apellido());
        paciente.actualizarDatos(pedido.dni(), pedido.fechaNacimiento());
        LOG.info("Paciente {} actualizado", idPaciente);
        return PacienteResponse.desde(paciente);
    }

    /** Solo se borra un paciente sin citas ni dispositivos (si no, 409). El expediente se borra en cascada. */
    @Transactional
    public void eliminar(Integer idPaciente) {
        Paciente paciente = buscarPaciente(idPaciente);
        if (citaRepository.existsByPaciente_IdUsuario(idPaciente)
                || dispositivoRepository.existsByPaciente_IdUsuario(idPaciente)) {
            throw new ReglaNegocioException("No se puede eliminar el paciente: tiene citas o dispositivos");
        }
        pacienteRepository.delete(paciente);
        LOG.info("Paciente {} eliminado", idPaciente);
    }

    /** Método del UML consultarCitas(). */
    @Transactional(readOnly = true)
    public List<CitaResponse> consultarCitas(Integer idPaciente) {
        return buscarPaciente(idPaciente).consultarCitas().stream().map(CitaResponse::desde).toList();
    }

    /** Método del UML consultarExpediente(). */
    @Transactional(readOnly = true)
    public ExpedienteResponse consultarExpediente(Integer idPaciente) {
        return ExpedienteResponse.desde(buscarPaciente(idPaciente).consultarExpediente());
    }

    /**
     * Método del UML consultarMetricas() (S6): las métricas están en metricas-service (MongoDB),
     * así que se piden por REST. Primero se controla que el paciente exista.
     */
    @Transactional(readOnly = true)
    public List<MetricaSaludResponse> consultarMetricas(Integer idPaciente, String tipo,
                                                        LocalDateTime desde, LocalDateTime hasta) {
        buscarPaciente(idPaciente);
        return metricasClient.consultarMetricas(idPaciente, tipo, desde, hasta);
    }

    /** Busca la entidad o lanza 404. Lo usan también otros services. */
    @Transactional(readOnly = true)
    public Paciente buscarPaciente(Integer idPaciente) {
        return pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el paciente " + idPaciente));
    }

    private void validarEmailLibre(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + email);
        }
    }
}
