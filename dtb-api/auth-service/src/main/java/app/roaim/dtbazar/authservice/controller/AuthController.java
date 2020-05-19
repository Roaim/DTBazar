package app.roaim.dtbazar.authservice.controller;

import app.roaim.dtbazar.authservice.dto.UserDto;
import app.roaim.dtbazar.authservice.jwt.JwtData;
import app.roaim.dtbazar.authservice.jwt.JwtToken;
import app.roaim.dtbazar.authservice.service.AuthService;
import app.roaim.dtbazar.authservice.service.FacebookService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final FacebookService facebookService;
    private final AuthService authService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<JwtToken> generateJwtToken(@RequestHeader("X-Facebook-Access-Token") String facebookAccessToken,
                                    @ApiIgnore ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-FORWARDED-FOR");
        return authService.getUserByFbAccessToken(facebookAccessToken)
                .switchIfEmpty(
                        facebookService.getFacebookUserProfile(facebookAccessToken)
                                .flatMap(fbUserProfile -> authService.saveGetUser(fbUserProfile, facebookAccessToken, xForwardedFor))
                ).flatMap(authService::generateJwtToken);
    }

    @GetMapping("/tokenInfo")
    Mono<JwtData> getTokenInfo(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken) {
        return authService.decodeJwt(bearerToken);
    }

    @GetMapping("/profile")
    Mono<UserDto> getProfileMe(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken) {
        return getTokenInfo(bearerToken).flatMap(jwtData ->
                authService.getUserById(jwtData.getSub())
        ).map(UserDto::fromUser);
    }
}
