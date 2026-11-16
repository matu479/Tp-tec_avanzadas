package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla profesional (unida con usuario por la herencia). */
public interface ProfesionalRepository extends JpaRepository<Profesional, Integer> {
}
