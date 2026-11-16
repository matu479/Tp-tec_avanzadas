package ar.edu.palermo.telemedicina.core.controller;

import ar.edu.palermo.telemedicina.core.dto.LoginRequest;
import ar.edu.palermo.telemedicina.core.dto.LoginResponse;
import ar.edu.palermo.telemedicina.core.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** CAPA DE PRESENTACIÓN. Login (método del UML iniciarSesion). */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 200 con los datos del usuario, o 401 si el email o la contraseña no coinciden. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest pedido) {
        return authService.iniciarSesion(pedido);
    }
}
