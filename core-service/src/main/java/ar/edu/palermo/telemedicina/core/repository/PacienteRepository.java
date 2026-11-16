package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla paciente (unida con usuario por la herencia). */
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
}
