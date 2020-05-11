package app.roaim.dtbazar.authservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@Configuration
@RefreshScope
@EnableSwagger2WebFlux
public class SwaggerConfig {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${spring.application.name}")
    private String appName;
    @Value("${dtbazar.gateway.host}")
    private String host;
    @Value("${dtbazar.api.version}")
    private String apiVersion;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host(format("%s/api/%s", host, apiVersion))
                .select()
                .apis(RequestHandlerSelectors.basePackage("app.roaim.dtbazar.authservice.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfoBuilder().version(apiVersion).title(appName.toUpperCase() + " API Documentation").build())
                .genericModelSubstitutes(ResponseEntity.class)
                .genericModelSubstitutes(Mono.class)
                .genericModelSubstitutes(Flux.class)
                .useDefaultResponseMessages(false).securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(apiKey()))
                .useDefaultResponseMessages(false);
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/auth/profile").or(PathSelectors.regex("/auth/tokenInfo")))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
                new SecurityReference("JWT", authorizationScopes));
    }
}
