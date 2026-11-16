package ar.edu.palermo.telemedicina.simulador;

/**
 * Configuración del simulador, leída de variables de entorno (con valores por defecto).
 */
public record Configuracion(String urlCore, String urlMetricas, int intervaloSegundos,
                            double probabilidadFueraDeRango, boolean escenarioCritico,
                            int segundosEscenarioCritico) {

    public static Configuracion desdeVariablesDeEntorno() {
        return new Configuracion(
                variable("CORE_URL", "http://localhost:8080"),
                variable("METRICAS_URL", "http://localhost:8081"),
                Integer.parseInt(variable("INTERVALO_SEGUNDOS", "5")),
                Double.parseDouble(variable("PROBABILIDAD_FUERA_RANGO", "0")),
                Boolean.parseBoolean(variable("ESCENARIO_CRITICO", "false")),
                Integer.parseInt(variable("SEGUNDOS_ESCENARIO_CRITICO", "20")));
    }

    private static String variable(String nombre, String valorPorDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? valorPorDefecto : valor;
    }
}
