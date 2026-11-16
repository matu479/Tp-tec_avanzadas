package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.dto.DispositivoRequest;
import ar.edu.palermo.telemedicina.core.dto.DispositivoResponse;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.model.DispositivoIot;
import ar.edu.palermo.telemedicina.core.model.EstadoDispositivo;
import ar.edu.palermo.telemedicina.core.model.Paciente;
import ar.edu.palermo.telemedicina.core.repository.DispositivoIotRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. Alta y consulta de dispositivos IoT; el simulador informa su actividad.
 */
@Service
public class DispositivoService {

    private static final Logger LOG = LoggerFactory.getLogger(DispositivoService.class);

    private final DispositivoIotRepository dispositivoRepository;
    private final PacienteService pacienteService;

    public DispositivoService(DispositivoIotRepository dispositivoRepository, PacienteService pacienteService) {
        this.dispositivoRepository = dispositivoRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional
    public DispositivoResponse crear(DispositivoRequest pedido) {
        Paciente paciente = pacienteService.buscarPaciente(pedido.idPaciente());
        DispositivoIot guardado = dispositivoRepository.save(
                new DispositivoIot(paciente, pedido.tipo().toUpperCase()));
        LOG.info("Dispositivo {} ({}) dado de alta para el paciente {}", guardado.getIdDispositivo(),
                guardado.getTipo(), paciente.getIdUsuario());
        return DispositivoResponse.desde(guardado);
    }

    /** Lista todos o solo los de un estado (el simulador pide ?estado=ACTIVO). */
    @Transactional(readOnly = true)
    public List<DispositivoResponse> listar(EstadoDispositivo estado) {
        List<DispositivoIot> dispositivos = estado == null
                ? dispositivoRepository.findAll()
                : dispositivoRepository.findByEstado(estado);
        return dispositivos.stream().map(DispositivoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public DispositivoResponse obtener(Integer idDispositivo) {
        return DispositivoResponse.desde(buscarDispositivo(idDispositivo));
    }

    @Transactional
    public DispositivoResponse cambiarEstado(Integer idDispositivo, EstadoDispositivo estado) {
        DispositivoIot dispositivo = buscarDispositivo(idDispositivo);
        dispositivo.cambiarEstado(estado);
        LOG.info("Dispositivo {} pasó a {}", idDispositivo, estado);
        return DispositivoResponse.desde(dispositivo);
    }

    /** Actualiza ultima_actividad (lo llama el simulador después de enviar una métrica). */
    @Transactional
    public DispositivoResponse registrarActividad(Integer idDispositivo) {
        DispositivoIot dispositivo = buscarDispositivo(idDispositivo);
        dispositivo.registrarActividad();
        return DispositivoResponse.desde(dispositivo);
    }

    private DispositivoIot buscarDispositivo(Integer idDispositivo) {
        return dispositivoRepository.findById(idDispositivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el dispositivo " + idDispositivo));
    }
}
