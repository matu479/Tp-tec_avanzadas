package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.AlertaRequest;
import ar.edu.palermo.telemedicina.core.dto.AlertaResponse;
import ar.edu.palermo.telemedicina.core.model.EstadoAlerta;
import ar.edu.palermo.telemedicina.core.service.AlertaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. Registro (lo llama notificaciones-service), listado y atención de alertas. */
@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertaResponse registrar(@Valid @RequestBody AlertaRequest pedido) {
        return alertaService.registrar(pedido);
    }

    /** GET /api/alertas?estado=GENERADA&idPaciente=4&idProfesional=3 (los tres filtros son opcionales) */
    @GetMapping
    public List<AlertaResponse> listar(@RequestParam(required = false) EstadoAlerta estado,
                                       @RequestParam(required = false) Integer idPaciente,
                                       @RequestParam(required = false) Integer idProfesional) {
        return alertaService.listar(estado, idPaciente, idProfesional);
    }

    /** marcarAtendida() */
    @PatchMapping("/{id}/atender")
    public AlertaResponse atender(@PathVariable Integer id) {
        return alertaService.atender(id);
    }
}
