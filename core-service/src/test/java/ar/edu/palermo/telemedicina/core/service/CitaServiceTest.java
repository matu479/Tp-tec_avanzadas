package ar.edu.palermo.telemedicina.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.palermo.telemedicina.core.dto.CitaRequest;
import ar.edu.palermo.telemedicina.core.dto.CitaResponse;
import ar.edu.palermo.telemedicina.core.exception.ReglaNegocioException;
import ar.edu.palermo.telemedicina.core.model.Cita;
import ar.edu.palermo.telemedicina.core.model.EstadoCita;
import ar.edu.palermo.telemedicina.core.model.Paciente;
import ar.edu.palermo.telemedicina.core.model.Profesional;
import ar.edu.palermo.telemedicina.core.repository.CitaRepository;
import ar.edu.palermo.telemedicina.core.repository.PacienteRepository;
import ar.edu.palermo.telemedicina.core.repository.ProfesionalRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TIPO DE TEST (apunte): UNITARIO CON MOCKS (Mockito).
 * Se prueba CitaService aislado: los repositorios son objetos simulados (mocks) que se
 * pasan por el constructor. Así se ve la ventaja de la inyección por constructor.
 */
class CitaServiceTest {

    private static final LocalDateTime FECHA = LocalDateTime.now().plusDays(3).withNano(0);

    private CitaRepository citaRepository;
    private CitaService citaService;

    @BeforeEach
    void prepararMocks() {
        citaRepository = mock(CitaRepository.class);
        PacienteRepository pacienteRepository = mock(PacienteRepository.class);
        ProfesionalRepository profesionalRepository = mock(ProfesionalRepository.class);
        when(pacienteRepository.findById(4)).thenReturn(Optional.of(
                new Paciente("p@tp.com", "clave", "Gómez", "Ana", "30111222", LocalDate.of(1983, 4, 12))));
        when(profesionalRepository.findById(3)).thenReturn(Optional.of(
                new Profesional("m@tp.com", "clave", "Martínez", "Carlos", "MN-1", "Cardiología")));
        citaService = new CitaService(citaRepository, pacienteRepository, profesionalRepository);
    }

    @Test
    void reservar_turnoLibre_guardaLaCitaReservada() {
        // Given: el profesional no tiene otra cita a esa hora
        when(citaRepository.existsByProfesional_IdUsuarioAndFechaHoraAndEstado(any(), any(), any()))
                .thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        // When
        CitaResponse respuesta = citaService.reservar(new CitaRequest(4, 3, FECHA, "Control"));
        // Then: se guardó y quedó RESERVADA
        verify(citaRepository).save(any(Cita.class));
        assertThat(respuesta.estado()).isEqualTo(EstadoCita.RESERVADA.name());
        assertThat(respuesta.fechaHora()).isEqualTo(FECHA);
    }

    @Test
    void reservar_turnoOcupado_lanzaReglaNegocioException() {
        // Given: el profesional ya tiene una cita RESERVADA a esa hora
        when(citaRepository.existsByProfesional_IdUsuarioAndFechaHoraAndEstado(any(), any(), any()))
                .thenReturn(true);
        // When / Then: se rechaza y no se guarda nada
        assertThatThrownBy(() -> citaService.reservar(new CitaRequest(4, 3, FECHA, "Control")))
                .isInstanceOf(ReglaNegocioException.class);
        verify(citaRepository, never()).save(any());
    }
}
