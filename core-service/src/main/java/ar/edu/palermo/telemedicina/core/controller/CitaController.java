package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.CitaRequest;
import ar.edu.palermo.telemedicina.core.dto.CitaResponse;
import ar.edu.palermo.telemedicina.core.dto.ReprogramarCitaRequest;
import ar.edu.palermo.telemedicina.core.service.CitaService;
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

/** CAPA DE PRESENTACIÓN. Reserva, consulta, cancelación y reprogramación de citas. */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    /** reservar(): 201, o 409 si el turno está ocupado, o 422 si la fecha es pasada. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponse reservar(@Valid @RequestBody CitaRequest pedido) {
        return citaService.reservar(pedido);
    }

    /** GET /api/citas (todas) o GET /api/citas?idProfesional=3 (agenda del profesional) */
    @GetMapping
    public List<CitaResponse> listar(@RequestParam(required = false) Integer idProfesional) {
        return citaService.listar(idProfesional);
    }

    @GetMapping("/{id}")
    public CitaResponse obtener(@PathVariable Integer id) {
        return citaService.obtener(id);
    }

    /** cancelar() */
    @PatchMapping("/{id}/cancelar")
    public CitaResponse cancelar(@PathVariable Integer id) {
        return citaService.cancelar(id);
    }

    /** reprogramar(fechaHora) */
    @PatchMapping("/{id}/reprogramar")
    public CitaResponse reprogramar(@PathVariable Integer id, @Valid @RequestBody ReprogramarCitaRequest pedido) {
        return citaService.reprogramar(id, pedido.fechaHora());
    }
}
