package ar.edu.palermo.telemedicina.core.model;

import ar.edu.palermo.telemedicina.core.exception.ReglaNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Alerta médica registrada en la base del core (S3).
 * La genera y la envía notificaciones-service; acá se guarda y se marca como atendida (S7).
 */
@Entity
@Table(name = "alerta_medica")
public class AlertaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false)
    private NivelAlerta nivel;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAlerta estado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    /** Puede ser null si el paciente no tiene citas (S12). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesional")
    private Profesional profesional;

    protected AlertaMedica() {
    }

    /** Registra una alerta recién generada (estado GENERADA). */
    public AlertaMedica(Paciente paciente, Profesional profesional, NivelAlerta nivel,
                        String mensaje, LocalDateTime fechaHora) {
        this.paciente = paciente;
        this.profesional = profesional;
        this.nivel = nivel;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.estado = EstadoAlerta.GENERADA;
    }

    /** Método del UML: marca la alerta como atendida (solo si no lo estaba). */
    public void marcarAtendida() {
        if (estado == EstadoAlerta.ATENDIDA) {
            throw new ReglaNegocioException("La alerta " + idAlerta + " ya estaba atendida");
        }
        this.estado = EstadoAlerta.ATENDIDA;
    }

    public Integer getIdAlerta() {
        return idAlerta;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getMensaje() {
        return mensaje;
    }

    public EstadoAlerta getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Profesional getProfesional() {
        return profesional;
    }
}
