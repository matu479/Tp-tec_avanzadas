package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.Cita;
import ar.edu.palermo.telemedicina.core.model.EstadoCita;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla cita. */
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    /** ¿El profesional ya tiene una cita en ese estado a esa hora? (turno ocupado) */
    boolean existsByProfesional_IdUsuarioAndFechaHoraAndEstado(
            Integer idProfesional, LocalDateTime fechaHora, EstadoCita estado);

    boolean existsByPaciente_IdUsuario(Integer idPaciente);

    boolean existsByProfesional_IdUsuario(Integer idProfesional);

    /** Agenda de un profesional, ordenada por fecha. */
    List<Cita> findByProfesional_IdUsuarioOrderByFechaHora(Integer idProfesional);

    /** Última cita del paciente (la de fecha más grande). Se usa para asignar alertas (S12). */
    Optional<Cita> findFirstByPaciente_IdUsuarioOrderByFechaHoraDesc(Integer idPaciente);
}
