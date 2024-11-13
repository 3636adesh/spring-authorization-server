package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
public class ResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class, args);
    }


    @Service
   static class GreetingService {

        public Map<String, String> greet() {
            var jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Map.of("message", "hello," + jwt.getSubject());
        }
    }

    @RestController
    static class GreetingRestController {


        private final GreetingService greetingService;

        public GreetingRestController(GreetingService greetingService) {
            this.greetingService = greetingService;
        }


        @PreAuthorize("hasAuthority('SCOPE_user.read')")
        @GetMapping
        public Map<String, String> greet() {
            return greetingService.greet();
        }
    }
}
