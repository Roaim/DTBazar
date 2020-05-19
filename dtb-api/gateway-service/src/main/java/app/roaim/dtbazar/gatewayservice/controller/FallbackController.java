package app.roaim.dtbazar.gatewayservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/{serviceName}")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    Mono<?> fallbackAnyService(@PathVariable String serviceName) {
        return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, serviceName + " may be down"));
    }
}

