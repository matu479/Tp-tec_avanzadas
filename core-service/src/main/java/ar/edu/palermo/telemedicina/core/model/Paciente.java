package ar.edu.palermo.telemedicina.core.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Paciente: hereda de Usuario (S1). Tiene un expediente clínico (1 a 1) y muchas citas.
 * {@code consultarMetricas()} del UML está en PacienteService, porque las métricas
 * viven en otro microservicio (S6).
 */
@Entity
@Table(name = "paciente")
@PrimaryKeyJoinColumn(name = "id_paciente")
public class Paciente extends Usuario {

    @Column(name = "dni", nullable = false)
    private String dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL)
    private ExpedienteClinico expediente;

    @OneToMany(mappedBy = "paciente")
    private List<Cita> citas = new ArrayList<>();

    protected Paciente() {
    }

    public Paciente(String email, String passwordPlano, String apellido, String nombre,
                    String dni, LocalDate fechaNacimiento) {
        super(email, passwordPlano, apellido, nombre, Rol.PACIENTE);
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
    }

    /** Método del UML: devuelve el expediente clínico del paciente. */
    public ExpedienteClinico consultarExpediente() {
        return expediente;
    }

    /** Método del UML: devuelve las citas del paciente (copia que no se puede modificar). */
    public List<Cita> consultarCitas() {
        return List.copyOf(citas);
    }

    /** Vincula el expediente con este paciente (se usa al dar de alta el paciente). */
    public void asignarExpediente(ExpedienteClinico expediente) {
        this.expediente = expediente;
    }

    /** Actualiza los datos propios del paciente. */
    public void actualizarDatos(String dni, LocalDate fechaNacimiento) {
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDni() {
        return dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
}
