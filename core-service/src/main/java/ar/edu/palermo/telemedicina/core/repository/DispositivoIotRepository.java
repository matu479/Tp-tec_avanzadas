package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.DispositivoIot;
import ar.edu.palermo.telemedicina.core.model.EstadoDispositivo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla dispositivo_iot. */
public interface DispositivoIotRepository extends JpaRepository<DispositivoIot, Integer> {

    List<DispositivoIot> findByEstado(EstadoDispositivo estado);

    boolean existsByPaciente_IdUsuario(Integer idPaciente);
}
