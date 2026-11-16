package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.AlertaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla alerta_medica. */
public interface AlertaMedicaRepository extends JpaRepository<AlertaMedica, Integer> {
}
