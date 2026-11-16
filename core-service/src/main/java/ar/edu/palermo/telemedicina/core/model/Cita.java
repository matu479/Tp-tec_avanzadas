package ar.edu.palermo.telemedicina.core.model;

import ar.edu.palermo.telemedicina.core.exception.DatoInvalidoException;
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
 * Cita (turno) entre un paciente y un profesional.
 * Sus métodos implementan la máquina de estados:
 * (nueva) --reservar--> RESERVADA --cancelar--> CANCELADA
 *                       RESERVADA --marcarAtendida--> ATENDIDA
 *                       RESERVADA --reprogramar--> RESERVADA (otra fecha)
 */
@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCita estado;

    @Column(name = "motivo")
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_profesional")
    private Profesional profesional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    protected Cita() {
    }

    /** Crea la cita sin estado: queda RESERVADA recién cuando se llama a {@link #reservar()}. */
    public Cita(Paciente paciente, Profesional profesional, LocalDateTime fechaHora, String motivo) {
        this.paciente = paciente;
        this.profesional = profesional;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
    }

    /** Método del UML: valida que la fecha sea futura y deja la cita RESERVADA. */
    public void reservar() {
        validarFechaFutura(fechaHora);
        this.estado = EstadoCita.RESERVADA;
    }

    /** Método del UML: solo se puede cancelar una cita RESERVADA. */
    public void cancelar() {
        validarReservada("cancelar");
        this.estado = EstadoCita.CANCELADA;
    }

    /** Método del UML: cambia la fecha de una cita RESERVADA a otra fecha futura. */
    public void reprogramar(LocalDateTime fechaHora) {
        validarReservada("reprogramar");
        validarFechaFutura(fechaHora);
        this.fechaHora = fechaHora;
    }

    /** Método agregado (no está en el UML): la cita pasa a ATENDIDA al registrar la consulta. */
    public void marcarAtendida() {
        validarReservada("atender");
        this.estado = EstadoCita.ATENDIDA;
    }

    private void validarReservada(String accion) {
        if (estado != EstadoCita.RESERVADA) {
            throw new ReglaNegocioException(
                    "No se puede " + accion + " la cita porque está " + estado);
        }
    }

    private static void validarFechaFutura(LocalDateTime fecha) {
        if (fecha == null || !fecha.isAfter(LocalDateTime.now())) {
            throw new DatoInvalidoException("La fecha y hora de la cita tiene que ser futura");
        }
    }

    public Integer getIdCita() {
        return idCita;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public Paciente getPaciente() {
        return paciente;
    }
}
