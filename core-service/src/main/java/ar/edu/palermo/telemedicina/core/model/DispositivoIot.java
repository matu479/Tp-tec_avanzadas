package ar.edu.palermo.telemedicina.core.model;

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
 * Dispositivo IoT de un paciente (entidad del DER).
 * {@code enviarMetrica()} del UML está en el simulador, que es quien envía (S8).
 */
@Entity
@Table(name = "dispositivo_iot")
public class DispositivoIot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo")
    private Integer idDispositivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDispositivo estado;

    /** Tipo de aparato: OXIMETRO, TENSIOMETRO, PULSERA o TERMOMETRO. */
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    protected DispositivoIot() {
    }

    /** Un dispositivo nuevo arranca ACTIVO. */
    public DispositivoIot(Paciente paciente, String tipo) {
        this.paciente = paciente;
        this.tipo = tipo;
        this.estado = EstadoDispositivo.ACTIVO;
    }

    /** Método del UML: devuelve el estado como texto. */
    public String obtenerEstado() {
        return estado.name();
    }

    /** Cambia el estado (ACTIVO / INACTIVO). */
    public void cambiarEstado(EstadoDispositivo estado) {
        this.estado = estado;
    }

    /** Registra que el dispositivo envió una métrica ahora. */
    public void registrarActividad() {
        this.ultimaActividad = LocalDateTime.now();
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }

    public EstadoDispositivo getEstado() {
        return estado;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public Paciente getPaciente() {
        return paciente;
    }
}
