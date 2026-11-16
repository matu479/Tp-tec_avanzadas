package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.dto.CitaRequest;
import ar.edu.palermo.telemedicina.core.dto.CitaResponse;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.exception.ReglaNegocioException;
import ar.edu.palermo.telemedicina.core.model.Cita;
import ar.edu.palermo.telemedicina.core.model.EstadoCita;
import ar.edu.palermo.telemedicina.core.model.Paciente;
import ar.edu.palermo.telemedicina.core.model.Profesional;
import ar.edu.palermo.telemedicina.core.repository.CitaRepository;
import ar.edu.palermo.telemedicina.core.repository.PacienteRepository;
import ar.edu.palermo.telemedicina.core.repository.ProfesionalRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. Casos de uso de citas: reservar, cancelar y reprogramar.
 * Las reglas de estado están en la entidad Cita; acá se valida el turno libre y se guarda.
 */
@Service
public class CitaService {

    private static final Logger LOG = LoggerFactory.getLogger(CitaService.class);

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfesionalRepository profesionalRepository;

    /** Inyección por constructor: en el test se pasan mocks de los repositorios. */
    public CitaService(CitaRepository citaRepository, PacienteRepository pacienteRepository,
                       ProfesionalRepository profesionalRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.profesionalRepository = profesionalRepository;
    }

    /** Método del UML reservar(): si el profesional ya tiene una cita RESERVADA a esa hora → 409. */
    @Transactional
    public CitaResponse reservar(CitaRequest pedido) {
        Paciente paciente = pacienteRepository.findById(pedido.idPaciente())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el paciente " + pedido.idPaciente()));
        Profesional profesional = profesionalRepository.findById(pedido.idProfesional())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el profesional " + pedido.idProfesional()));
        validarTurnoLibre(profesional.getIdUsuario(), pedido.fechaHora());
        Cita cita = new Cita(paciente, profesional, pedido.fechaHora(), pedido.motivo());
        cita.reservar();
        Cita guardada = citaRepository.save(cita);
        LOG.info("Cita {} reservada: paciente {} con profesional {} el {}", guardada.getIdCita(),
                paciente.getIdUsuario(), profesional.getIdUsuario(), guardada.getFechaHora());
        return CitaResponse.desde(guardada);
    }

    /** Todas las citas, o solo las de un profesional (su agenda) si se indica idProfesional. */
    @Transactional(readOnly = true)
    public List<CitaResponse> listar(Integer idProfesional) {
        List<Cita> citas = idProfesional == null
                ? citaRepository.findAll(Sort.by("fechaHora"))
                : citaRepository.findByProfesional_IdUsuarioOrderByFechaHora(idProfesional);
        return citas.stream().map(CitaResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public CitaResponse obtener(Integer idCita) {
        return CitaResponse.desde(buscarCita(idCita));
    }

    /** Método del UML cancelar(). */
    @Transactional
    public CitaResponse cancelar(Integer idCita) {
        Cita cita = buscarCita(idCita);
        cita.cancelar();
        LOG.info("Cita {} cancelada", idCita);
        return CitaResponse.desde(cita);
    }

    /** Método del UML reprogramar(fechaHora): también controla que el nuevo turno esté libre. */
    @Transactional
    public CitaResponse reprogramar(Integer idCita, LocalDateTime nuevaFechaHora) {
        Cita cita = buscarCita(idCita);
        validarTurnoLibre(cita.getProfesional().getIdUsuario(), nuevaFechaHora);
        cita.reprogramar(nuevaFechaHora);
        LOG.info("Cita {} reprogramada para el {}", idCita, nuevaFechaHora);
        return CitaResponse.desde(cita);
    }

    /** Busca la entidad o lanza 404. Lo usa también ConsultaService. */
    @Transactional(readOnly = true)
    public Cita buscarCita(Integer idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cita " + idCita));
    }

    private void validarTurnoLibre(Integer idProfesional, LocalDateTime fechaHora) {
        if (citaRepository.existsByProfesional_IdUsuarioAndFechaHoraAndEstado(
                idProfesional, fechaHora, EstadoCita.RESERVADA)) {
            throw new ReglaNegocioException("El profesional ya tiene una cita reservada el " + fechaHora);
        }
    }
}
