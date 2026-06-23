package com.ecommerce.api_gateway.config;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;


@Configuration
public class GatewayConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);
    }


    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/v1/product/**").uri("lb://PRODUCT-SERVICE"))
                .route("order-service", r -> r.path("/api/v1/order/**").uri("lb://ORDER-SERVICE"))
                .route("inventory-service", r -> r.path("/api/v1/inventory/**").uri("lb://INVENTORY-SERVICE"))
                .build();

    }
}