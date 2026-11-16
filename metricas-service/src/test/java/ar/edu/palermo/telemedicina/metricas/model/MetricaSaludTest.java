package ar.edu.palermo.telemedicina.metricas.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * TIPO DE TEST (apunte): UNITARIO + ANÁLISIS DE VALORES LÍMITE.
 * Rango normal de pulsaciones: 50 a 110 lpm. Se prueba justo en cada límite,
 * uno abajo y uno arriba, que es donde suelen estar los errores (< en vez de <=).
 */
class MetricaSaludTest {

    private static MetricaSalud pulsaciones(double valor) {
        return new MetricaSalud(4, 2, TipoMetrica.PULSACIONES, valor, "lpm", LocalDateTime.now());
    }

    @ParameterizedTest(name = "pulsaciones={0} → superaUmbral={1}")
    @CsvSource({
        "49,  true",    // uno abajo del límite inferior
        "50,  false",   // justo en el límite inferior
        "51,  false",   // uno arriba del límite inferior
        "109, false",   // uno abajo del límite superior
        "110, false",   // justo en el límite superior
        "111, true"     // uno arriba del límite superior
    })
    void superaUmbral_pulsacionesEnLosLimites_devuelveLoEsperado(double valor, boolean esperado) {
        // Given
        MetricaSalud metrica = pulsaciones(valor);
        // When
        boolean resultado = metrica.superaUmbral();
        // Then
        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void validar_valorImposible_devuelveFalse() {
        // Given: 300 lpm no es físicamente posible (rango 20 a 250)
        MetricaSalud metrica = pulsaciones(300);
        // When / Then
        assertThat(metrica.validar()).isFalse();
    }
}
