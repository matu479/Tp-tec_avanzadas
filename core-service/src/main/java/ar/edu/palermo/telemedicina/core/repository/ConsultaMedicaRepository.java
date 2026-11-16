package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.ConsultaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla consulta (el diagnóstico se guarda en cascada). */
public interface ConsultaMedicaRepository extends JpaRepository<ConsultaMedica, Integer> {
}
