package ar.edu.palermo.telemedicina.simulador;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dispositivo IoT del UML (S8): es quien envía las métricas.
 */
public class DispositivoIot {

    private static final Logger LOG = Logger.getLogger(DispositivoIot.class.getName());

    private final Integer idDispositivo;
    private final Integer idPaciente;
    private final String tipo;
    private final String estado;
    private LocalDateTime ultimaActividad;
    private final ClienteApi clienteApi;

    public DispositivoIot(Integer idDispositivo, Integer idPaciente, String tipo, String estado,
                          ClienteApi clienteApi) {
        this.idDispositivo = idDispositivo;
        this.idPaciente = idPaciente;
        this.tipo = tipo;
        this.estado = estado;
        this.clienteApi = clienteApi;
    }

    /**
     * Método del UML: envía la métrica a metricas-service. Si sale bien, actualiza
     * ultimaActividad y le avisa al core.
     */
    public void enviarMetrica(MetricaSalud metrica) {
        try {
            int codigo = clienteApi.enviarMetrica(metrica);
            if (codigo != 201) {
                LOG.warning("Dispositivo " + idDispositivo + ": metricas-service respondió " + codigo);
                return;
            }
            ultimaActividad = LocalDateTime.now();
            clienteApi.notificarActividad(idDispositivo);
            LOG.info("Dispositivo " + idDispositivo + " (" + tipo + ", paciente " + idPaciente + ") envió " + metrica);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Dispositivo " + idDispositivo + ": no se pudo enviar la métrica", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /** Método del UML. */
    public String obtenerEstado() {
        return estado;
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }
}
