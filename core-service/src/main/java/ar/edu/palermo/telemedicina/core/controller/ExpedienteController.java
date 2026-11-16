package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.ExpedienteRequest;
import ar.edu.palermo.telemedicina.core.dto.ExpedienteResponse;
import ar.edu.palermo.telemedicina.core.service.ExpedienteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. Ver y editar expedientes clínicos. */
@RestController
@RequestMapping("/api/expedientes")
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    public ExpedienteController(ExpedienteService expedienteService) {
        this.expedienteService = expedienteService;
    }

    @GetMapping("/{id}")
    public ExpedienteResponse obtener(@PathVariable Integer id) {
        return expedienteService.obtener(id);
    }

    @PutMapping("/{id}")
    public ExpedienteResponse actualizar(@PathVariable Integer id, @RequestBody ExpedienteRequest pedido) {
        return expedienteService.actualizar(id, pedido);
    }
}
