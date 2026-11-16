package ar.edu.palermo.telemedicina.core.dto;

import ar.edu.palermo.telemedicina.core.model.Usuario;

/** Datos del usuario que inició sesión (nunca se devuelve la contraseña). */
public record LoginResponse(Integer idUsuario, String email, String nombre, String apellido, String rol) {

    public static LoginResponse desde(Usuario usuario) {
        return new LoginResponse(usuario.getIdUsuario(), usuario.getEmail(), usuario.getNombre(),
                usuario.getApellido(), usuario.getRol().name());
    }
}
