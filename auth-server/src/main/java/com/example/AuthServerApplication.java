package com.example;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

@EnableConfigurationProperties(RSAKeyProperties.class)
@SpringBootApplication
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

}

@Configuration
class AppConfig {

    private final RSAKeyProperties rsaKeyProperties;

    AppConfig(RSAKeyProperties rsaKeyProperties) {
        this.rsaKeyProperties = rsaKeyProperties;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults());
        return http.build();
    }

    @Bean
    RegisteredClientRepository registeredClientRepository() {
        var client1 = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client-1")
                .clientSecret("{noop}password1")
                .clientName("John Doe")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .scope("read").build();

        var client2 = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client-2")
                .clientSecret("{noop}password2")
                .clientName("Alice Doe")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .scope("read").build();


        return new InMemoryRegisteredClientRepository(client1, client2);
    }


    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey()).privateKey(rsaKeyProperties.privateKey()).keyID(UUID.randomUUID().toString()).build();

        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JWKSet jwkSet() {
        JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey()).keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).keyID("public-key-id").build();

        return new JWKSet(jwk);
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                var registeredClient = context.getRegisteredClient();
                var claims = context.getClaims();
                claims.issuer("public-key-id");
                claims.claims(claim -> {
                    claim.put("scope", registeredClient.getScopes());
                });

            }
        };
    }
}


@ConfigurationProperties(prefix = "rsa")
record RSAKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {

}

@RestController
class JWKSetController {


    private final JWKSet jwkSet;

    JWKSetController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwkSet() {
        return jwkSet.toJSONObject();
    }

}