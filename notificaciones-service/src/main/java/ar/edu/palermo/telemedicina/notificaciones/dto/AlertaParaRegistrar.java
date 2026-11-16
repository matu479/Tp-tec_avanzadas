package ar.edu.palermo.telemedicina.notificaciones.dto;

import ar.edu.palermo.telemedicina.notificaciones.model.AlertaMedica;
import java.time.LocalDateTime;

/** Lo que se le manda al core para que registre la alerta (POST /api/alertas). */
public record AlertaParaRegistrar(Integer idPaciente, String nivel, String mensaje, LocalDateTime fechaHora) {

    public static AlertaParaRegistrar desde(AlertaMedica alerta) {
        return new AlertaParaRegistrar(alerta.getIdPaciente(), alerta.getNivel(), alerta.getMensaje(),
                alerta.getFechaHora());
    }
}
