package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.ConsultaRequest;
import ar.edu.palermo.telemedicina.core.dto.ConsultaResponse;
import ar.edu.palermo.telemedicina.core.dto.DiagnosticoResponse;
import ar.edu.palermo.telemedicina.core.dto.TextoRequest;
import ar.edu.palermo.telemedicina.core.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. Consultas médicas: registro, diagnóstico, indicaciones y observaciones. */
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /** registrarConsulta(): 201, o 409 si la cita no está RESERVADA. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaResponse registrar(@Valid @RequestBody ConsultaRequest pedido) {
        return consultaService.registrar(pedido);
    }

    @GetMapping("/{id}")
    public ConsultaResponse obtener(@PathVariable Integer id) {
        return consultaService.obtener(id);
    }

    /** registrarDiagnostico(String) */
    @PutMapping("/{id}/diagnostico")
    public DiagnosticoResponse registrarDiagnostico(@PathVariable Integer id,
                                                    @Valid @RequestBody TextoRequest pedido) {
        return consultaService.registrarDiagnostico(id, pedido.texto());
    }

    /** agregarIndicacion(String) */
    @PatchMapping("/{id}/indicaciones")
    public ConsultaResponse agregarIndicacion(@PathVariable Integer id, @Valid @RequestBody TextoRequest pedido) {
        return consultaService.agregarIndicacion(id, pedido.texto());
    }

    /** agregarObservacion(String) */
    @PatchMapping("/{id}/observaciones")
    public ConsultaResponse agregarObservacion(@PathVariable Integer id, @Valid @RequestBody TextoRequest pedido) {
        return consultaService.agregarObservacion(id, pedido.texto());
    }
}
