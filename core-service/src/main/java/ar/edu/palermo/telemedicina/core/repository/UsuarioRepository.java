package ar.edu.palermo.telemedicina.core.repository;

import ar.edu.palermo.telemedicina.core.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** CAPA DE DATOS. Acceso a la tabla usuario. */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
