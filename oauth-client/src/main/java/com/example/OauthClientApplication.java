package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OauthClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthClientApplication.class, args);
    }


    @Bean
    RouteLocator routes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(rs->rs
                        .path("/")
                        .filters(GatewayFilterSpec::tokenRelay)
                        .uri("http://localhost:8081"))
                .build();
    }

}
