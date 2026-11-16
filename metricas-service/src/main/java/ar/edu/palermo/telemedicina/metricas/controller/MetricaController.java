package ar.edu.palermo.telemedicina.metricas.controller;

import ar.edu.palermo.telemedicina.metricas.dto.MetricaRequest;
import ar.edu.palermo.telemedicina.metricas.dto.MetricaResponse;
import ar.edu.palermo.telemedicina.metricas.model.TipoMetrica;
import ar.edu.palermo.telemedicina.metricas.service.MetricaService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. Recepción de datos IoT y consulta de métricas. */
@RestController
@RequestMapping("/api/metricas")
public class MetricaController {

    private final MetricaService metricaService;

    public MetricaController(MetricaService metricaService) {
        this.metricaService = metricaService;
    }

    /** Recepción de datos IoT: 201 sin esperar a notificaciones, o 422 si el valor es imposible. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetricaResponse registrar(@Valid @RequestBody MetricaRequest pedido) {
        return metricaService.registrar(pedido);
    }

    /** Consulta de métricas (la usa el core). GET /api/metricas?idPaciente=4&tipo=SATURACION */
    @GetMapping
    public List<MetricaResponse> consultar(
            @RequestParam Integer idPaciente,
            @RequestParam(required = false) TipoMetrica tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return metricaService.consultar(idPaciente, tipo, desde, hasta);
    }
}
