package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.ExpedienteClinico;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla expediente_clinico. */
public interface ExpedienteClinicoRepository extends JpaRepository<ExpedienteClinico, Integer> {
}
