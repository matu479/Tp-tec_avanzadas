package ar.edu.palermo.telemedicina.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ar.edu.palermo.telemedicina.core.exception.ReglaNegocioException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * TIPO DE TEST (apunte): STATE TRANSITION (transición de estados).
 * Se recorre la máquina de estados de la Cita: transiciones válidas e inválidas.
 * Es un test unitario puro: no usa Spring ni base de datos.
 */
class CitaTest {

    private static final LocalDateTime MANIANA = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime EN_UNA_SEMANA = LocalDateTime.now().plusDays(7);

    /** Crea una cita nueva y la reserva (estado inicial: RESERVADA). */
    private static Cita citaReservada() {
        Paciente paciente = new Paciente("p@tp.com", "clave", "Pérez", "Juan", "1", LocalDate.of(1990, 1, 1));
        Profesional profesional = new Profesional("m@tp.com", "clave", "López", "Ana", "MN-1", "Clínica");
        Cita cita = new Cita(paciente, profesional, MANIANA, "Control");
        cita.reservar();
        return cita;
    }

    static Stream<Arguments> transicionesValidas() {
        return Stream.of(
                Arguments.of("RESERVADA --cancelar--> CANCELADA",
                        (Consumer<Cita>) Cita::cancelar, EstadoCita.CANCELADA),
                Arguments.of("RESERVADA --reprogramar--> RESERVADA",
                        (Consumer<Cita>) cita -> cita.reprogramar(EN_UNA_SEMANA), EstadoCita.RESERVADA),
                Arguments.of("RESERVADA --marcarAtendida--> ATENDIDA",
                        (Consumer<Cita>) Cita::marcarAtendida, EstadoCita.ATENDIDA));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("transicionesValidas")
    void transicion_desdeReservada_llegaAlEstadoEsperado(String nombre, Consumer<Cita> accion,
                                                         EstadoCita estadoEsperado) {
        // Given: una cita reservada
        Cita cita = citaReservada();
        // When: se aplica la transición
        accion.accept(cita);
        // Then: queda en el estado esperado
        assertThat(cita.getEstado()).isEqualTo(estadoEsperado);
    }

    static Stream<Arguments> transicionesInvalidas() {
        return Stream.of(
                Arguments.of("cancelar una cita CANCELADA",
                        (Consumer<Cita>) Cita::cancelar, (Consumer<Cita>) Cita::cancelar),
                Arguments.of("reprogramar una cita ATENDIDA",
                        (Consumer<Cita>) Cita::marcarAtendida,
                        (Consumer<Cita>) cita -> cita.reprogramar(EN_UNA_SEMANA)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("transicionesInvalidas")
    void transicion_invalida_lanzaReglaNegocioException(String nombre, Consumer<Cita> llevarAlEstado,
                                                       Consumer<Cita> accionInvalida) {
        // Given: una cita que ya salió de RESERVADA
        Cita cita = citaReservada();
        llevarAlEstado.accept(cita);
        EstadoCita estadoAntes = cita.getEstado();
        // When / Then: la transición inválida se rechaza y el estado no cambia
        assertThatThrownBy(() -> accionInvalida.accept(cita))
                .isInstanceOf(ReglaNegocioException.class);
        assertThat(cita.getEstado()).isEqualTo(estadoAntes);
    }
}
