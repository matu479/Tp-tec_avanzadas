package ar.edu.palermo.telemedicina.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Profesional de la salud: hereda de Usuario (S1).
 */
@Entity
@Table(name = "profesional")
@PrimaryKeyJoinColumn(name = "id_profesional")
public class Profesional extends Usuario {

    @Column(name = "matricula", nullable = false)
    private String matricula;

    @Column(name = "especialidad", nullable = false)
    private String especialidad;

    @OneToMany(mappedBy = "profesional")
    private List<ConsultaMedica> consultas = new ArrayList<>();

    @OneToMany(mappedBy = "profesional")
    private List<AlertaMedica> alertas = new ArrayList<>();

    protected Profesional() {
    }

    public Profesional(String email, String passwordPlano, String apellido, String nombre,
                       String matricula, String especialidad) {
        super(email, passwordPlano, apellido, nombre, Rol.PROFESIONAL);
        this.matricula = matricula;
        this.especialidad = especialidad;
    }

    /** Método del UML: asocia una consulta médica a este profesional. */
    public void registrarConsulta(ConsultaMedica consulta) {
        consultas.add(consulta);
    }

    /**
     * Método del UML: devuelve las alertas asignadas a este profesional que todavía
     * no fueron atendidas (S5: el UML dice void, pero una revisión tiene que devolver algo).
     */
    public List<AlertaMedica> revisarAlertas() {
        return alertas.stream()
                .filter(alerta -> alerta.getEstado() != EstadoAlerta.ATENDIDA)
                .toList();
    }

    /** Método del UML: el historial de un paciente es su expediente clínico. */
    public ExpedienteClinico consultarHistorial(Paciente paciente) {
        return paciente.consultarExpediente();
    }

    /** Actualiza los datos propios del profesional. */
    public void actualizarDatos(String matricula, String especialidad) {
        this.matricula = matricula;
        this.especialidad = especialidad;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getEspecialidad() {
        return especialidad;
    }
}
