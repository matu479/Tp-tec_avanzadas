package ar.edu.palermo.telemedicina.metricas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de metricas-service (procesamiento de métricas en tiempo real).
 */
@SpringBootApplication
public class MetricasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricasApplication.class, args);
    }
}
