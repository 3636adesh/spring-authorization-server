package com.example.resorce_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ResorceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResorceServerApplication.class, args);
    }

}


@Configuration
class AppConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/").hasAnyAuthority("SCOPE_read"))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        String issuerUri = "http://localhost:8080/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(issuerUri).build();
    }
}

@RestController
class WelcomeController {

    @GetMapping
    public String welcome() {
        return "Welcome to the Resource Server!";
    }
}