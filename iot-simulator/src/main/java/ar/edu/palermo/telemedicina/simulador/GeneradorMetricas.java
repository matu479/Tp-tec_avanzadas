package ar.edu.palermo.telemedicina.simulador;

import java.util.Random;

/**
 * Genera valores al azar: casi siempre normales y, con una probabilidad configurable
 * (por defecto 0: nunca; con 0.05 sería un 5 %), uno fuera de rango.
 * Cada tipo de aparato mide una métrica: OXIMETRO→SATURACION, TENSIOMETRO→PRESION,
 * PULSERA→PULSACIONES, TERMOMETRO→TEMPERATURA.
 */
public class GeneradorMetricas {

    private final Random azar = new Random();
    private final double probabilidadFueraDeRango;

    public GeneradorMetricas(double probabilidadFueraDeRango) {
        this.probabilidadFueraDeRango = probabilidadFueraDeRango;
    }

    /** Devuelve una métrica para el dispositivo, o null si el tipo de aparato no se conoce. */
    public MetricaSalud generar(DispositivoIot dispositivo) {
        boolean fueraDeRango = azar.nextDouble() < probabilidadFueraDeRango;
        return switch (dispositivo.getTipo()) {
            case "OXIMETRO" -> crear(dispositivo, "SATURACION", "%", fueraDeRango ? entre(85, 92) : entre(95, 99));
            case "TENSIOMETRO" -> crear(dispositivo, "PRESION", "mmHg", fueraDeRango ? entre(150, 190) : entre(100, 135));
            case "PULSERA" -> crear(dispositivo, "PULSACIONES", "lpm", fueraDeRango ? entre(120, 160) : entre(60, 100));
            case "TERMOMETRO" -> crear(dispositivo, "TEMPERATURA", "°C", fueraDeRango ? entre(38, 40) : entre(36, 37.3));
            default -> null;
        };
    }

    /** Saturación de 87 % para la demo del escenario crítico. */
    public MetricaSalud saturacionCritica(DispositivoIot oximetro) {
        return crear(oximetro, "SATURACION", "%", 87);
    }

    private static MetricaSalud crear(DispositivoIot dispositivo, String tipo, String unidad, double valor) {
        return new MetricaSalud(dispositivo.getIdDispositivo(), dispositivo.getIdPaciente(), tipo, valor, unidad);
    }

    /** Número al azar entre min y max, redondeado a 1 decimal. */
    private double entre(double min, double max) {
        return Math.round((min + azar.nextDouble() * (max - min)) * 10) / 10.0;
    }
}
