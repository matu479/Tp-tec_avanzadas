package ar.edu.palermo.telemedicina.metricas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita @Async: el evento de riesgo se envía en otro hilo, así la API de métricas
 * responde 201 sin esperar a notificaciones-service.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
