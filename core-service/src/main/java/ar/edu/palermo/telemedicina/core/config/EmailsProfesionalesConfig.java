package ar.edu.palermo.telemedicina.core.config;

import ar.edu.palermo.telemedicina.core.service.ProfesionalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Permite cambiar por variable de entorno el email de los 2 profesionales de prueba
 * (por si se quiere recibir la alerta en un mail real). Si las variables están vacías, no hace nada.
 */
@Configuration
public class EmailsProfesionalesConfig {

    @Bean
    public ApplicationRunner configurarEmailsProfesionales(ProfesionalService profesionalService,
            @Value("${demo.email-profesional-clinica:}") String emailClinica,
            @Value("${demo.email-profesional-cardio:}") String emailCardio) {
        return argumentos -> {
            profesionalService.cambiarEmailDePrueba(2, emailClinica);
            profesionalService.cambiarEmailDePrueba(3, emailCardio);
        };
    }
}
