package ar.edu.palermo.telemedicina.notificaciones.controller;

import ar.edu.palermo.telemedicina.notificaciones.dto.EventoRiesgo;
import ar.edu.palermo.telemedicina.notificaciones.service.GestorEventosService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. Recibe los eventos de riesgo de metricas-service. */
@RestController
@RequestMapping("/api")
public class EventoRiesgoController {

    private final GestorEventosService gestorEventosService;

    public EventoRiesgoController(GestorEventosService gestorEventosService) {
        this.gestorEventosService = gestorEventosService;
    }

    /** 202 Accepted: "lo recibí, lo proceso en segundo plano". */
    @PostMapping("/eventos-riesgo")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> recibir(@Valid @RequestBody EventoRiesgo evento) {
        gestorEventosService.procesar(evento);
        return Map.of("mensaje", "Evento de riesgo recibido");
    }
}
