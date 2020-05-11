package app.roaim.dtbazar.authservice.controller;

import app.roaim.dtbazar.authservice.model.FbAccessToken;
import app.roaim.dtbazar.authservice.model.FbAccessTokenError;
import app.roaim.dtbazar.authservice.model.FbAuthorizationUrl;
import app.roaim.dtbazar.authservice.service.FacebookService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;

@RestController
@RequestMapping("/auth/facebook")
@AllArgsConstructor
public class FacebookAuthController {
    private final FacebookService facebookService;

    @GetMapping("/authorizationUrl")
    public Mono<FbAuthorizationUrl> generateFacebookAuthorizationUrl() {
        return facebookService.createFacebookAuthorizationUrl().map(FbAuthorizationUrl::new);
    }

    @GetMapping("/authorize")
    public Mono<Void> redirectToAuthorizationUrl(@ApiIgnore ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create(facebookService.generateFacebookAuthorizationUrl()));
        return response.setComplete();
    }

    @GetMapping("/accessToken")
    public Mono<FbAccessToken> generateFacebookAccessTokenFromAuthorizationCode(
            @RequestParam(required = false) String code,
            @ApiIgnore @RequestParam(required = false) String error,
            @ApiIgnore @RequestParam(value = "error_code", required = false) String errorCode,
            @ApiIgnore @RequestParam(value = "error_description", required = false) String errorDescription,
            @ApiIgnore @RequestParam(value = "error_reason", required = false) String errorReason
    ) {
        if (code != null) {
            return facebookService.createFacebookAccessToken(code).map(FbAccessToken::fromAccessGrant);
        } else {
            return Mono.just(new FbAccessTokenError(error, errorCode, errorDescription, errorReason))
                    .flatMap(eb -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, eb.toString())));
        }
    }
}
