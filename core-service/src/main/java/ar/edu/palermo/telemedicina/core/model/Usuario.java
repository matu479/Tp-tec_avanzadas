package ar.edu.palermo.telemedicina.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Usuario del sistema (clase base del UML). Paciente y Profesional la extienden (S1).
 * Un usuario ADMIN es un Usuario "a secas".
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    /** Encoder BCrypt: transforma la contraseña en un hash que no se puede revertir. */
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /** Se guarda el hash BCrypt, nunca la contraseña en texto plano (S11). */
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    /** Constructor vacío que exige JPA. */
    protected Usuario() {
    }

    /**
     * Crea un usuario. La contraseña se recibe en texto plano y se guarda hasheada.
     */
    public Usuario(String email, String passwordPlano, String apellido, String nombre, Rol rol) {
        this.email = email;
        this.password = ENCODER.encode(passwordPlano);
        this.apellido = apellido;
        this.nombre = nombre;
        this.rol = rol;
    }

    /**
     * Método del UML. Compara la contraseña ingresada contra el hash guardado (S5).
     *
     * @return true si la contraseña es correcta
     */
    public boolean iniciarSesion(String passwordIngresado) {
        return passwordIngresado != null && ENCODER.matches(passwordIngresado, password);
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Rol getRol() {
        return rol;
    }
}
