package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.DispositivoIot;
import java.time.LocalDateTime;

/** Datos de un dispositivo IoT. */
public record DispositivoResponse(Integer id, Integer idPaciente, String tipo, String estado,
                                  LocalDateTime ultimaActividad) {

    public static DispositivoResponse desde(DispositivoIot dispositivo) {
        return new DispositivoResponse(dispositivo.getIdDispositivo(),
                dispositivo.getPaciente().getIdUsuario(), dispositivo.getTipo(),
                dispositivo.obtenerEstado(), dispositivo.getUltimaActividad());
    }
}
