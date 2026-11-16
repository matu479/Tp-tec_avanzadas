package ar.edu.palermo.telemedicina.simulador;

import java.time.LocalDateTime;

/**
 * Copia simple de la métrica que se envía a metricas-service (mismos campos que el JSON).
 */
public class MetricaSalud {

    private final Integer idDispositivo;
    private final Integer idPaciente;
    private final String tipo;
    private final double valor;
    private final String unidad;
    private final LocalDateTime fechaHora;

    public MetricaSalud(Integer idDispositivo, Integer idPaciente, String tipo, double valor, String unidad) {
        this.idDispositivo = idDispositivo;
        this.idPaciente = idPaciente;
        this.tipo = tipo;
        this.valor = valor;
        this.unidad = unidad;
        this.fechaHora = LocalDateTime.now();
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

    public double getValor() {
        return valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    @Override
    public String toString() {
        return tipo + "=" + valor + " " + unidad;
    }
}
