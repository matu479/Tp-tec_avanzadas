package ar.edu.palermo.telemedicina.core.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Consulta médica (tabla {@code consulta} del DER). Se registra al atender una cita
 * y queda guardada en el expediente del paciente.
 */
@Entity
@Table(name = "consulta")
public class ConsultaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Integer idConsulta;

    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta;

    @Column(name = "indicaciones", length = 2000)
    private String indicaciones;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cita")
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_profesional")
    private Profesional profesional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_expediente")
    private ExpedienteClinico expediente;

    /** 0..1 diagnóstico por consulta (S2). Se guarda junto con la consulta (cascade). */
    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL)
    private Diagnostico diagnostico;

    protected ConsultaMedica() {
    }

    public ConsultaMedica(Cita cita, ExpedienteClinico expediente,
                          String observaciones, String indicaciones) {
        this.cita = cita;
        this.profesional = cita.getProfesional();
        this.expediente = expediente;
        this.observaciones = observaciones;
        this.indicaciones = indicaciones;
        this.fechaConsulta = LocalDateTime.now();
    }

    /** Método del UML: si no hay diagnóstico lo crea; si ya hay, actualiza la descripción (S2). */
    public void registrarDiagnostico(String diagnostico) {
        if (this.diagnostico == null) {
            this.diagnostico = new Diagnostico(this, diagnostico);
        } else {
            this.diagnostico.actualizarDescripcion(diagnostico);
        }
    }

    /** Método del UML: agrega una indicación al final del texto existente (no lo pisa). */
    public void agregarIndicacion(String indicacion) {
        this.indicaciones = agregarTexto(indicaciones, indicacion);
    }

    /** Método del UML: agrega una observación al final del texto existente (no lo pisa). */
    public void agregarObservacion(String observacion) {
        this.observaciones = agregarTexto(observaciones, observacion);
    }

    private static String agregarTexto(String textoActual, String nuevo) {
        if (textoActual == null || textoActual.isBlank()) {
            return nuevo;
        }
        return textoActual + "\n" + nuevo;
    }

    public Integer getIdConsulta() {
        return idConsulta;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Cita getCita() {
        return cita;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public ExpedienteClinico getExpediente() {
        return expediente;
    }

    public Diagnostico getDiagnostico() {
        return diagnostico;
    }
}
