package ar.edu.palermo.telemedicina.simulador;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Llamadas HTTP del simulador con java.net.http.HttpClient + Jackson:
 * pedir dispositivos al core, enviar métricas a metricas-service y avisar actividad al core.
 */
public class ClienteApi {

    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private final ObjectMapper json = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final Configuracion configuracion;

    public ClienteApi(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    /** GET core /api/dispositivos?estado=ACTIVO */
    public List<DispositivoIot> obtenerDispositivosActivos() throws IOException, InterruptedException {
        HttpRequest pedido = HttpRequest.newBuilder(URI.create(configuracion.urlCore()
                + "/api/dispositivos?estado=ACTIVO")).timeout(TIMEOUT).GET().build();
        HttpResponse<String> respuesta = http.send(pedido, HttpResponse.BodyHandlers.ofString());
        List<DispositivoIot> dispositivos = new ArrayList<>();
        for (JsonNode nodo : json.readTree(respuesta.body())) {
            dispositivos.add(new DispositivoIot(nodo.get("id").asInt(), nodo.get("idPaciente").asInt(),
                    nodo.get("tipo").asText(), nodo.get("estado").asText(), this));
        }
        return dispositivos;
    }

    /** POST metricas /api/metricas. Devuelve el código HTTP (201 si se guardó). */
    public int enviarMetrica(MetricaSalud metrica) throws IOException, InterruptedException {
        HttpRequest pedido = HttpRequest.newBuilder(URI.create(configuracion.urlMetricas() + "/api/metricas"))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(metrica)))
                .build();
        return http.send(pedido, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    /** PATCH core /api/dispositivos/{id}/actividad */
    public void notificarActividad(Integer idDispositivo) throws IOException, InterruptedException {
        HttpRequest pedido = HttpRequest.newBuilder(URI.create(configuracion.urlCore()
                        + "/api/dispositivos/" + idDispositivo + "/actividad"))
                .timeout(TIMEOUT)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        http.send(pedido, HttpResponse.BodyHandlers.discarding());
    }
}
