package com.example.auth_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


/**
 * This class demonstrates setting up a Spring Authorization Server.
 * For more details, see the official documentation:
 * {@link <a href="https://docs.spring.io/spring-authorization-server/reference/getting-started.html">Spring doc</a>}
 * {@link <a href="https://github.com/spring-tips/spring-authorization-server">GitHub Repository</a>}
 */



@SpringBootApplication
public class AuthServerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

	@Bean
	InMemoryUserDetailsManager inMemoryUserDetailsManager(){
		var builder = User.builder();
		var one = builder.username("one").password("{noop}pw").roles("admin", "user").build();
		var two = builder.username("two").password("{noop}pw").roles("user").build();
		return new InMemoryUserDetailsManager(one, two);
	}

}
