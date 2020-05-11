package app.roaim.dtbazar.storeservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import java.net.URI;

@Service
@RefreshScope
public class MediaDeleteService {
    @Value("${dtbazar.url.scheme}")
    private String scheme;
    @Value("${dtbazar.gateway.host}")
    private String host;
    @Value("${dtbazar.api.version}")
    private String apiVersion;

    private final WebClient webClient;

    public MediaDeleteService(WebClient webClient) {
        this.webClient = webClient;
    }

    private String getMediaUrl(String id) {
        return String.format("%s://%s/api/%s/media/%s", scheme, host, apiVersion, id);
    }

    public void delete(String id, String bearerToken) {
        webClient.delete()
                .uri(URI.create(getMediaUrl(id)))
                .header("Authorization", bearerToken)
                .retrieve().toBodilessEntity().block();

    }
}
