package ar.edu.palermo.telemedicina.notificaciones.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** Cliente HTTP hacia el core (registro de alertas), con timeout de 3 segundos. */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient coreRestClient(@Value("${core.url}") String urlCore) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(3));
        fabrica.setReadTimeout(Duration.ofSeconds(3));
        return RestClient.builder().baseUrl(urlCore).requestFactory(fabrica).build();
    }
}
