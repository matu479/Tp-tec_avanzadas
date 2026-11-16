package ar.edu.palermo.telemedicina.simulador;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Programa principal del simulador IoT: pide al core los dispositivos activos y, cada
 * INTERVALO_SEGUNDOS (5 s), hace que cada uno envíe una métrica.
 * Con ESCENARIO_CRITICO=true, a los 20 segundos manda una saturación de 87 % (demo).
 */
public class Simulador {

    private static final Logger LOG = Logger.getLogger(Simulador.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Configuracion configuracion = Configuracion.desdeVariablesDeEntorno();
        ClienteApi clienteApi = new ClienteApi(configuracion);
        GeneradorMetricas generador = new GeneradorMetricas(configuracion.probabilidadFueraDeRango());
        List<DispositivoIot> dispositivos = esperarDispositivos(clienteApi);
        LOG.info("Simulador iniciado con " + dispositivos.size() + " dispositivos activos");

        ScheduledExecutorService reloj = Executors.newSingleThreadScheduledExecutor();
        reloj.scheduleAtFixedRate(() -> enviarRonda(dispositivos, generador),
                0, configuracion.intervaloSegundos(), TimeUnit.SECONDS);
        if (configuracion.escenarioCritico()) {
            LOG.info("ESCENARIO CRÍTICO activado: saturación de 87 % en "
                    + configuracion.segundosEscenarioCritico() + " s");
            reloj.schedule(() -> enviarSaturacionCritica(dispositivos, generador),
                    configuracion.segundosEscenarioCritico(), TimeUnit.SECONDS);
        }
    }

    /** Cada dispositivo envía una métrica. */
    private static void enviarRonda(List<DispositivoIot> dispositivos, GeneradorMetricas generador) {
        for (DispositivoIot dispositivo : dispositivos) {
            MetricaSalud metrica = generador.generar(dispositivo);
            if (metrica == null) {
                LOG.warning("Tipo de dispositivo desconocido: " + dispositivo.getTipo());
            } else {
                dispositivo.enviarMetrica(metrica);
            }
        }
    }

    /** Demo: el primer oxímetro manda una saturación de 87 %. */
    private static void enviarSaturacionCritica(List<DispositivoIot> dispositivos, GeneradorMetricas generador) {
        dispositivos.stream()
                .filter(dispositivo -> "OXIMETRO".equals(dispositivo.getTipo()))
                .findFirst()
                .ifPresentOrElse(oximetro -> {
                    LOG.warning("ESCENARIO CRÍTICO: enviando saturación de 87 % del paciente "
                            + oximetro.getIdPaciente());
                    oximetro.enviarMetrica(generador.saturacionCritica(oximetro));
                }, () -> LOG.warning("ESCENARIO CRÍTICO: no hay ningún OXIMETRO activo"));
    }

    /** Reintenta cada 5 s hasta que el core responda con la lista de dispositivos. */
    private static List<DispositivoIot> esperarDispositivos(ClienteApi clienteApi) throws InterruptedException {
        while (true) {
            try {
                return clienteApi.obtenerDispositivosActivos();
            } catch (Exception ex) {
                LOG.warning("El core todavía no responde (" + ex.getMessage() + "); reintento en 5 s");
                Thread.sleep(5_000);
            }
        }
    }
}
