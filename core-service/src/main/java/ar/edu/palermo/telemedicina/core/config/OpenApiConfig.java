package ar.edu.palermo.telemedicina.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración de Swagger (springdoc): título y descripción de la API. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiCore() {
        return new OpenAPI().info(new Info()
                .title("core-service - Backend principal")
                .description("Usuarios, pacientes, profesionales, citas, expedientes, consultas, "
                        + "dispositivos IoT y alertas")
                .version("1.0.0"));
    }
}
