package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.CitaResponse;
import ar.edu.palermo.telemedicina.core.dto.ExpedienteResponse;
import ar.edu.palermo.telemedicina.core.dto.MetricaSaludResponse;
import ar.edu.palermo.telemedicina.core.dto.PacienteRequest;
import ar.edu.palermo.telemedicina.core.dto.PacienteResponse;
import ar.edu.palermo.telemedicina.core.service.PacienteService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. ABM de pacientes, sus citas y su expediente. Sin lógica de negocio. */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteResponse crear(@Valid @RequestBody PacienteRequest pedido) {
        return pacienteService.crear(pedido);
    }

    @GetMapping
    public List<PacienteResponse> listar() {
        return pacienteService.listar();
    }

    @GetMapping("/{id}")
    public PacienteResponse obtener(@PathVariable Integer id) {
        return pacienteService.obtener(id);
    }

    @PutMapping("/{id}")
    public PacienteResponse actualizar(@PathVariable Integer id, @Valid @RequestBody PacienteRequest pedido) {
        return pacienteService.actualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        pacienteService.eliminar(id);
    }

    /** consultarCitas() */
    @GetMapping("/{id}/citas")
    public List<CitaResponse> consultarCitas(@PathVariable Integer id) {
        return pacienteService.consultarCitas(id);
    }

    /** consultarExpediente() */
    @GetMapping("/{id}/expediente")
    public ExpedienteResponse consultarExpediente(@PathVariable Integer id) {
        return pacienteService.consultarExpediente(id);
    }

    /** consultarMetricas(): consulta a metricas-service. 503 si no responde. */
    @GetMapping("/{id}/metricas")
    public List<MetricaSaludResponse> consultarMetricas(
            @PathVariable Integer id,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return pacienteService.consultarMetricas(id, tipo, desde, hasta);
    }
}
