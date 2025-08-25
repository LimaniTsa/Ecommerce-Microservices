package com.ecommerce.gateway.config;

import java.time.Duration;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            //product service routes
            .route("product-service", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("product-service-cb")
                        .setFallbackUri("forward:/fallback/product-service"))
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(2000), 2, true)))
                .uri("lb://product-service"))
            
            //order service routes
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("order-service-cb")
                        .setFallbackUri("forward:/fallback/order-service"))
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(2000), 2, true)))
                .uri("lb://order-service"))
            
            //eureka dashboard route
            .route("eureka-server", r -> r
                .path("/eureka/web/**")
                .filters(f -> f.rewritePath("/eureka/web/(?<path>.*)", "/${path}"))
                .uri("http://localhost:8761"))
            .route("eureka-server-main", r -> r
                .path("/eureka/**")
                .uri("http://localhost:8761"))
            
            //config server route
            .route("config-server", r -> r
                .path("/config/**")
                .uri("http://localhost:8888"))
            
            .build();
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just("default-user");
    }
}