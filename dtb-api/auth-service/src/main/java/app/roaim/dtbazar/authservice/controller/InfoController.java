package app.roaim.dtbazar.authservice.controller;

import app.roaim.dtbazar.authservice.model.IpInfo;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.util.Objects;

@RestController
public class InfoController {

    @GetMapping("/ipInfo")
    Mono<IpInfo> getIpInfo(@ApiIgnore ServerHttpRequest request) {
        String url = String.format("http://ip-api.com/json/%s", getClientIp(request));
        return WebClient.create().get()
                .uri(URI.create(url))
                .retrieve().bodyToMono(IpInfo.class);
    }

    private static String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-FORWARDED-FOR");
        if (xForwardedFor == null) {
            return Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        }
        if (xForwardedFor.contains(",")) {
            String[] ips = xForwardedFor.split(",");
            return ips[0].trim();
        }
        return xForwardedFor;
    }
}
