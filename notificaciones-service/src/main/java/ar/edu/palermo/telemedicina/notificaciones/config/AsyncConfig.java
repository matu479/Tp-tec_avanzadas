package ar.edu.palermo.telemedicina.notificaciones.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Habilita @Async: el evento se procesa en segundo plano y la API responde 202 enseguida. */
@Configuration
@EnableAsync
public class AsyncConfig {
}
