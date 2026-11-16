package ar.edu.palermo.telemedicina.metricas.repository;

import ar.edu.palermo.telemedicina.metricas.model.MetricaSalud;
import ar.edu.palermo.telemedicina.metricas.model.TipoMetrica;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/** CAPA DE DATOS. Acceso a la colección metricas_salud de MongoDB. */
public interface MetricaSaludRepository extends MongoRepository<MetricaSalud, String> {

    List<MetricaSalud> findByIdPacienteAndFechaHoraBetweenOrderByFechaHoraDesc(
            Integer idPaciente, LocalDateTime desde, LocalDateTime hasta);

    List<MetricaSalud> findByIdPacienteAndTipoAndFechaHoraBetweenOrderByFechaHoraDesc(
            Integer idPaciente, TipoMetrica tipo, LocalDateTime desde, LocalDateTime hasta);
}
