package app.roaim.dtbazar.gatewayservice;

import org.springframework.cloud.gateway.filter.factory.HystrixGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    /*private final RedisRateLimiter redisRateLimiter;

    public Routes(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }*/

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**", "/doc/v1/auth", "/api/v1/ipInfo", "/api/v1/admin/user/**",
								"/api/v1/privacy.html", "/api/v1/terms.html")
                        .filters(f -> f
//                                .requestRateLimiter(this::configureRateLimiter)
                                .hystrix(h -> configureHystrix("auth-service", h))
                                .rewritePath("/api/v1/(?<path>.*)", "/${path}")
                                .rewritePath("/doc/v1/auth", "/v2/api-docs")
                        )
                        .uri("lb://auth-service")
                )
                .route("store-service", r -> r
                        .path("/api/v1/store/**", "/api/v1/food/**", "/api/v1/foodSell/**",
                                "/api/v1/storeFood/**", "/api/v1/donation/**", "/doc/v1/store", "/api/v1/admin/**")
                        .filters(f -> f
//                                .requestRateLimiter(this::configureRateLimiter)
                                .hystrix(h -> configureHystrix("store-service", h))
                                .rewritePath("/api/v1/(?<path>.*)", "/${path}")
                                .rewritePath("/doc/v1/store", "/v2/api-docs")
                        )
                        .uri("lb://store-service")
                )
                .route("media-service", r -> r
                        .path("/api/v1/media/**", "/doc/v1/media")
                        .filters(f -> f
//                                .requestRateLimiter(this::configureRateLimiter)
                                .hystrix(h -> configureHystrix("media-service", h))
                                .rewritePath("/api/v1/(?<path>.*)", "/${path}")
                                .rewritePath("/doc/v1/media", "/v2/api-docs")
                        )
                        .uri("lb://media-service")
                )
                .build();
    }

    private void configureHystrix(String serviceName, HystrixGatewayFilterFactory.Config config) {
        config
                .setName(serviceName)
                .setFallbackUri("forward:/fallback/" + serviceName);

    }

    /*private void configureRateLimiter(RequestRateLimiterGatewayFilterFactory.Config config) {
        config
                .setRateLimiter(redisRateLimiter)
                .setKeyResolver(exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName()));

    }*/

}
