package app.roaim.dtbazar.mediaservice.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final SecurityContextRepository securityContextRepository;

    @Bean
    SecurityWebFilterChain authorization(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .exceptionHandling(this::handleExceptions)
                .cors().and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/actuator/refresh").permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

    private void handleExceptions(ServerHttpSecurity.ExceptionHandlingSpec exceptionHandlingSpec) {
        exceptionHandlingSpec
                .accessDeniedHandler((swe, e) -> Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage())))
                .authenticationEntryPoint((swe, e) -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage())));
    }
}
