package ar.edu.palermo.telemedicina.metricas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración de Swagger (springdoc). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiMetricas() {
        return new OpenAPI().info(new Info()
                .title("metricas-service - Procesamiento de Métricas en Tiempo Real")
                .description("Recepción de datos IoT, evaluación de umbrales y consulta de métricas")
                .version("1.0.0"));
    }
}
