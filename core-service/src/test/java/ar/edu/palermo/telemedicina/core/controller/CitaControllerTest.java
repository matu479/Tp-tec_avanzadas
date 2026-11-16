package ar.edu.palermo.telemedicina.core.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.palermo.telemedicina.core.dto.CitaRequest;
import ar.edu.palermo.telemedicina.core.dto.CitaResponse;
import ar.edu.palermo.telemedicina.core.dto.ProfesionalResponse;
import ar.edu.palermo.telemedicina.core.exception.RecursoNoEncontradoException;
import ar.edu.palermo.telemedicina.core.service.CitaService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * TIPO DE TEST (apunte): INTEGRACIÓN DE LA CAPA DE PRESENTACIÓN.
 * @WebMvcTest levanta solo la parte web de Spring (controller + validación + GlobalExceptionHandler)
 * y simula pedidos HTTP con MockMvc. El service es un mock: no hay base de datos.
 */
@WebMvcTest(CitaController.class)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CitaService citaService;

    @Test
    void postCitas_bodyValido_responde201() throws Exception {
        // Given: el service reserva sin problemas
        LocalDateTime fecha = LocalDateTime.now().plusDays(2).withNano(0);
        ProfesionalResponse profesional = new ProfesionalResponse(3, "cardio@tp.com", "Carlos", "Martínez",
                "MN-20871", "Cardiología");
        when(citaService.reservar(any(CitaRequest.class)))
                .thenReturn(new CitaResponse(10, fecha, "RESERVADA", "Control", 4, "Ana Gómez", profesional));
        String body = "{\"idPaciente\":4,\"idProfesional\":3,\"fechaHora\":\"" + fecha + "\",\"motivo\":\"Control\"}";
        // When / Then
        mockMvc.perform(post("/api/citas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.estado").value("RESERVADA"));
    }

    @Test
    void postCitas_bodyInvalido_responde400() throws Exception {
        // Given: falta idPaciente y fechaHora (obligatorios)
        String body = "{\"idProfesional\":3,\"motivo\":\"Control\"}";
        // When / Then: Bean Validation lo rechaza antes de llegar al service
        mockMvc.perform(post("/api/citas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getCita_inexistente_responde404() throws Exception {
        // Given: la cita 999 no existe
        when(citaService.obtener(999)).thenThrow(new RecursoNoEncontradoException("No existe la cita 999"));
        // When / Then
        mockMvc.perform(get("/api/citas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe la cita 999"))
                .andExpect(jsonPath("$.path").value("/api/citas/999"));
    }
}
