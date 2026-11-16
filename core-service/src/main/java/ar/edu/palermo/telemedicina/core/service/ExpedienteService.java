package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.dto.ExpedienteRequest;
import ar.edu.palermo.telemedicina.core.dto.ExpedienteResponse;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.model.ExpedienteClinico;
import ar.edu.palermo.telemedicina.core.repository.ExpedienteClinicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. Ver y editar un expediente clínico.
 */
@Service
public class ExpedienteService {

    private static final Logger LOG = LoggerFactory.getLogger(ExpedienteService.class);

    private final ExpedienteClinicoRepository expedienteRepository;

    public ExpedienteService(ExpedienteClinicoRepository expedienteRepository) {
        this.expedienteRepository = expedienteRepository;
    }

    @Transactional(readOnly = true)
    public ExpedienteResponse obtener(Integer idExpediente) {
        return ExpedienteResponse.desde(buscarExpediente(idExpediente));
    }

    @Transactional
    public ExpedienteResponse actualizar(Integer idExpediente, ExpedienteRequest pedido) {
        ExpedienteClinico expediente = buscarExpediente(idExpediente);
        expediente.actualizar(pedido.antecedentes(), pedido.observacionesGenerales());
        LOG.info("Expediente {} actualizado", idExpediente);
        return ExpedienteResponse.desde(expediente);
    }

    private ExpedienteClinico buscarExpediente(Integer idExpediente) {
        return expedienteRepository.findById(idExpediente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el expediente " + idExpediente));
    }
}
