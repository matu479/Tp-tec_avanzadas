package ar.edu.palermo.telemedicina.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Diagnóstico de una consulta (tabla del DER, relación 0..1 con la consulta, S2).
 */
@Entity
@Table(name = "diagnostico")
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diagnostico")
    private Integer idDiagnostico;

    @Column(name = "descripcion", nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_consulta", unique = true)
    private ConsultaMedica consulta;

    protected Diagnostico() {
    }

    /** Lo crea {@link ConsultaMedica#registrarDiagnostico(String)}. */
    Diagnostico(ConsultaMedica consulta, String descripcion) {
        this.consulta = consulta;
        this.descripcion = descripcion;
        this.fecha = LocalDate.now();
    }

    /** Cambia la descripción y actualiza la fecha. */
    void actualizarDescripcion(String descripcion) {
        this.descripcion = descripcion;
        this.fecha = LocalDate.now();
    }

    public Integer getIdDiagnostico() {
        return idDiagnostico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
