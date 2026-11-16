package ar.edu.palermo.telemedicina.notificaciones.dto;

/** Lo que devuelve el core al registrar la alerta (solo los campos que se usan acá). */
public record AlertaRegistrada(Integer id, Integer idProfesional, String emailProfesional, String nombrePaciente) {
}
