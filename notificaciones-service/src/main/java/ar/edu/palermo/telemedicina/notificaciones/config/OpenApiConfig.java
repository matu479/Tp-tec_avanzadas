package ar.edu.palermo.telemedicina.notificaciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración de Swagger (springdoc). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiNotificaciones() {
        return new OpenAPI().info(new Info()
                .title("notificaciones-service - Notificaciones y Alertas Médicas")
                .description("Gestión de eventos, generación, registro y envío de alertas por email")
                .version("1.0.0"));
    }
}
