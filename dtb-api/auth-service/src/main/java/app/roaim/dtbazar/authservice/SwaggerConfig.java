package app.roaim.dtbazar.authservice;

import app.roaim.dtbazar.authservice.model.ErrorBody;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Arrays;
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
    @Value("${dtbazar.url.scheme}")
    private String scheme;
    @Value("${dtbazar.gateway.host}")
    private String host;
    @Value("${dtbazar.api.version}")
    private String apiVersion;

    @Bean
    public Docket api(TypeResolver typeResolver) {
        String gatewayUrl = format("%s/api/%s", host, apiVersion);
        String termsUrl = format("%s://%s/terms.html", scheme, gatewayUrl);
        return new Docket(DocumentationType.SWAGGER_2)
                .host(gatewayUrl)
                .select()
                .apis(RequestHandlerSelectors.basePackage("app.roaim.dtbazar.authservice.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfoBuilder()
                        .version(apiVersion)
                        .title(appName.toUpperCase() + " API Documentation")
                        .description("Expand the following controllers to view list of endpoints. " +
                                "Expand a endpoint to view its request details or try testing on the fly. The locked endpoints must be authorized with Bearer token." +
                                "You can switch across different services' documentations from the dropdown menu in the top right corner.")
                        .contact(new Contact("Roaim Ahmed Hridoy", "https://www.linkedin.com/in/roaim", "roaimahmed@ymail.com"))
                        .termsOfServiceUrl(termsUrl)
                        .license("GNU General Public License v3.0")
                        .licenseUrl("https://github.com/Roaim/DTBazar/blob/master/LICENSE")
                        .build()
                )
                .genericModelSubstitutes(ResponseEntity.class)
                .genericModelSubstitutes(Mono.class)
                .genericModelSubstitutes(Flux.class)
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(apiKey()))
                .additionalModels(typeResolver.resolve(ErrorBody.class))
                .globalResponseMessage(RequestMethod.GET, getResponseMegList())
                .globalResponseMessage(RequestMethod.POST, getResponseMegList())
                .globalResponseMessage(RequestMethod.PUT, getResponseMegList())
                .globalResponseMessage(RequestMethod.DELETE, getResponseMegList())
                .globalResponseMessage(RequestMethod.PATCH, getResponseMegList())
                .useDefaultResponseMessages(false);
    }

    private List<ResponseMessage> getResponseMegList() {
        return Arrays.asList(
                getResponseMsg(400, "Bad Request"),
                getResponseMsg(401, "Unauthorized"),
                getResponseMsg(403, "Forbidden"),
                getResponseMsg(404, "Not Found"),
                getResponseMsg(500, "Internal Server Error"),
                getResponseMsg(503, "Service Unavailable", "ErrorBody")
        );
    }

    private ResponseMessage getResponseMsg(int code, String msg) {
        return getResponseMsg(code, msg, null);
    }

    private ResponseMessage getResponseMsg(int code, String msg, String modelRef) {
        ResponseMessageBuilder messageBuilder = new ResponseMessageBuilder()
                .code(code)
                .message(msg);
        if (modelRef != null) {
            messageBuilder.responseModel(new ModelRef(modelRef));
        }
        return messageBuilder.build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/auth/profile")
                        .or(PathSelectors.regex("/auth/tokenInfo"))
                        .or(PathSelectors.regex("/admin.*"))
                )
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
