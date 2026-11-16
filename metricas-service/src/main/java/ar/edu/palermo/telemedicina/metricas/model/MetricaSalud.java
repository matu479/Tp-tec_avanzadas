package ar.edu.palermo.telemedicina.metricas.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Métrica de salud enviada por un dispositivo IoT. Un documento por medición en la
 * colección {@code metricas_salud} de MongoDB. Suma idPaciente e idDispositivo (S4).
 */
@Document(collection = "metricas_salud")
public class MetricaSalud {

    /** Id generado por Mongo (ObjectId como texto). */
    @Id
    private String idMetrica;
    private TipoMetrica tipo;
    private double valor;
    private String unidad;
    private LocalDateTime fechaHora;
    private Integer idPaciente;
    private Integer idDispositivo;

    /** Constructor vacío que usa Spring Data para leer de Mongo. */
    protected MetricaSalud() {
    }

    public MetricaSalud(Integer idPaciente, Integer idDispositivo, TipoMetrica tipo, double valor,
                        String unidad, LocalDateTime fechaHora) {
        this.idPaciente = idPaciente;
        this.idDispositivo = idDispositivo;
        this.tipo = tipo;
        this.valor = valor;
        this.unidad = unidad != null ? unidad : tipo.getUnidad();
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
    }

    /** Método del UML: el valor está dentro del rango físicamente posible del tipo. */
    public boolean validar() {
        return tipo.esPosible(valor);
    }

    /** Método del UML: el valor está fuera del rango normal del tipo. */
    public boolean superaUmbral() {
        return tipo.estaFueraDeLoNormal(valor);
    }

    /** Nivel de la alerta que corresponde: CRITICA si supera el umbral crítico, si no ALTA. */
    public NivelAlerta calcularNivel() {
        return tipo.esCritico(valor) ? NivelAlerta.CRITICA : NivelAlerta.ALTA;
    }

    public String getIdMetrica() {
        return idMetrica;
    }

    public TipoMetrica getTipo() {
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

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }
}
