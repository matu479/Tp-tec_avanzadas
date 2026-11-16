package ar.edu.palermo.telemedicina.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Expediente clínico: uno por paciente (FK UNIQUE en el DER). Junta todas las consultas.
 */
@Entity
@Table(name = "expediente_clinico")
public class ExpedienteClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_expediente")
    private Integer idExpediente;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "antecedentes", length = 2000)
    private String antecedentes;

    @Column(name = "observaciones_generales", length = 2000)
    private String observacionesGenerales;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_paciente", unique = true)
    private Paciente paciente;

    @OneToMany(mappedBy = "expediente")
    private List<ConsultaMedica> consultas = new ArrayList<>();

    protected ExpedienteClinico() {
    }

    /** Crea un expediente vacío para el paciente, con fecha de hoy. */
    public ExpedienteClinico(Paciente paciente) {
        this.paciente = paciente;
        this.fechaCreacion = LocalDate.now();
    }

    /** Método del UML: agrega una consulta al expediente. */
    public void agregarConsulta(ConsultaMedica consulta) {
        consultas.add(consulta);
    }

    /** Método del UML: consultas ordenadas de la más reciente a la más vieja. */
    public List<ConsultaMedica> obtenerHistorial() {
        return consultas.stream()
                .sorted(Comparator.comparing(ConsultaMedica::getFechaConsulta).reversed())
                .toList();
    }

    /** Edita los textos del expediente. */
    public void actualizar(String antecedentes, String observacionesGenerales) {
        this.antecedentes = antecedentes;
        this.observacionesGenerales = observacionesGenerales;
    }

    public Integer getIdExpediente() {
        return idExpediente;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public String getAntecedentes() {
        return antecedentes;
    }

    public String getObservacionesGenerales() {
        return observacionesGenerales;
    }

    public Paciente getPaciente() {
        return paciente;
    }
}
