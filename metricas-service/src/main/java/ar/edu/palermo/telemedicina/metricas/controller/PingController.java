package ar.edu.palermo.telemedicina.metricas.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CAPA DE PRESENTACIÓN. Endpoint mínimo para comprobar que el servicio está vivo.
 */
@RestController
@RequestMapping("/api")
public class PingController {

    /** Responde siempre lo mismo: sirve para probar que el contenedor arrancó. */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("servicio", "metricas-service", "estado", "OK");
    }
}
