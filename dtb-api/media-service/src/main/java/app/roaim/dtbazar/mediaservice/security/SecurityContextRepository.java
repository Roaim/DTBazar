package app.roaim.dtbazar.mediaservice.security;

import app.roaim.dtbazar.mediaservice.jwt.JWTUtil;
import app.roaim.dtbazar.mediaservice.jwt.JwtData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private JWTUtil jwtUtil;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return Mono.empty();
        } else if (authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            JwtData jwtData = jwtUtil.getJwtData(authToken);
            return jwtUtil.getAuthentication(jwtData).map(SecurityContextImpl::new);
        } else {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization header must start with Bearer"));
        }
    }
}