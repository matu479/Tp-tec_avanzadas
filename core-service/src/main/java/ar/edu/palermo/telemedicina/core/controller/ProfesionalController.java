package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.AlertaResponse;
import ar.edu.palermo.telemedicina.core.dto.ExpedienteResponse;
import ar.edu.palermo.telemedicina.core.dto.ProfesionalRequest;
import ar.edu.palermo.telemedicina.core.dto.ProfesionalResponse;
import ar.edu.palermo.telemedicina.core.service.ProfesionalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. ABM de profesionales e historial de pacientes. */
@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    public ProfesionalController(ProfesionalService profesionalService) {
        this.profesionalService = profesionalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfesionalResponse crear(@Valid @RequestBody ProfesionalRequest pedido) {
        return profesionalService.crear(pedido);
    }

    @GetMapping
    public List<ProfesionalResponse> listar() {
        return profesionalService.listar();
    }

    @GetMapping("/{id}")
    public ProfesionalResponse obtener(@PathVariable Integer id) {
        return profesionalService.obtener(id);
    }

    @PutMapping("/{id}")
    public ProfesionalResponse actualizar(@PathVariable Integer id,
                                          @Valid @RequestBody ProfesionalRequest pedido) {
        return profesionalService.actualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        profesionalService.eliminar(id);
    }

    /** revisarAlertas(): alertas no atendidas asignadas a este profesional. */
    @GetMapping("/{id}/alertas")
    public List<AlertaResponse> revisarAlertas(@PathVariable Integer id) {
        return profesionalService.revisarAlertas(id);
    }

    /** consultarHistorial(paciente) */
    @GetMapping("/{id}/historial/{idPaciente}")
    public ExpedienteResponse consultarHistorial(@PathVariable Integer id, @PathVariable Integer idPaciente) {
        return profesionalService.consultarHistorial(id, idPaciente);
    }
}
