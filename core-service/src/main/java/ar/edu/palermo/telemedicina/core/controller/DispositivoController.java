package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.DispositivoRequest;
import ar.edu.palermo.telemedicina.core.dto.DispositivoResponse;
import ar.edu.palermo.telemedicina.core.dto.EstadoDispositivoRequest;
import ar.edu.palermo.telemedicina.core.model.EstadoDispositivo;
import ar.edu.palermo.telemedicina.core.service.DispositivoService;
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

/** CAPA DE PRESENTACIÓN. Dispositivos IoT (el simulador usa el listado y /actividad). */
@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DispositivoResponse crear(@Valid @RequestBody DispositivoRequest pedido) {
        return dispositivoService.crear(pedido);
    }

    /** GET /api/dispositivos?estado=ACTIVO */
    @GetMapping
    public List<DispositivoResponse> listar(@RequestParam(required = false) EstadoDispositivo estado) {
        return dispositivoService.listar(estado);
    }

    @GetMapping("/{id}")
    public DispositivoResponse obtener(@PathVariable Integer id) {
        return dispositivoService.obtener(id);
    }

    @PatchMapping("/{id}/estado")
    public DispositivoResponse cambiarEstado(@PathVariable Integer id,
                                             @Valid @RequestBody EstadoDispositivoRequest pedido) {
        return dispositivoService.cambiarEstado(id, pedido.estado());
    }

    @PatchMapping("/{id}/actividad")
    public DispositivoResponse registrarActividad(@PathVariable Integer id) {
        return dispositivoService.registrarActividad(id);
    }
}
