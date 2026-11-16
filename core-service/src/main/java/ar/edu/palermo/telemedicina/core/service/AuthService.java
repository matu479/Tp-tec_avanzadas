package ar.edu.palermo.telemedicina.core.service;

import ar.edu.palermo.telemedicina.core.dto.LoginRequest;
import ar.edu.palermo.telemedicina.core.dto.LoginResponse;
import ar.edu.palermo.telemedicina.core.exception.CredencialesInvalidasException;
import ar.edu.palermo.telemedicina.core.model.Usuario;
import ar.edu.palermo.telemedicina.core.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO. Caso de uso "iniciar sesión" (S11: no hay tokens, solo se validan las credenciales).
 */
@Service
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Busca el usuario por email y delega la comparación de la contraseña en Usuario.iniciarSesion(). */
    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest pedido) {
        Usuario usuario = usuarioRepository.findByEmail(pedido.email())
                .filter(encontrado -> encontrado.iniciarSesion(pedido.password()))
                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));
        LOG.info("Inicio de sesión correcto: usuario {} ({})", usuario.getIdUsuario(), usuario.getRol());
        return LoginResponse.desde(usuario);
    }
}
