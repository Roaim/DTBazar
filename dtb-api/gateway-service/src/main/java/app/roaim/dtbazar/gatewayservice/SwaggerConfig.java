package app.roaim.dtbazar.gatewayservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfig {

    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            List<SwaggerResource> list = new ArrayList<>();
//            list.add(createResource("gateway", 1));
            list.add(createResource("auth", 1));
            list.add(createResource("store", 1));
            list.add(createResource("media", 1));
            return list;
        };
    }

    private SwaggerResource createResource(String name, int version) {
        return createResource(format("%s-v%d", name, version), format("/doc/v%d/%s", version, name), version);
    }

    private SwaggerResource createResource(String name, String location, double version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(String.valueOf(version));
        return swaggerResource;
    }
}