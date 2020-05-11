package app.roaim.dtbazar.gatewayservice.controller;

import app.roaim.dtbazar.gatewayservice.model.Fallback;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/{serviceName}")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    Mono<Fallback> fallbackAnyService(@PathVariable String serviceName) {
        return Mono.just(new Fallback(HttpStatus.SERVICE_UNAVAILABLE.value(), String.format("Failed to connect to the %s service", serviceName)));
    }
}

