package io.agibalov;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EnableWebSecurity
    public static class ResourceServerConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .mvcMatcher("/api/**")
                    .authorizeRequests()
                        .mvcMatchers("/api/**").access("hasAuthority('SCOPE_hello:read')")
                        .and()
                    .oauth2ResourceServer()
                        .jwt();
        }

        @Bean
        public JwtDecoder jwtDecoder(@Value("${app.secret}") String secret) {
            return NimbusJwtDecoder
                    .withSecretKey(
                            new SecretKeySpec(
                                    Base64.getDecoder().decode(secret),
                                    "AES"))
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }
    }

    @Bean
    public DummyController dummyController() {
        return new DummyController();
    }

    @Slf4j
    @RequestMapping
    @ResponseBody
    public static class DummyController {
        @GetMapping("/api/hello")
        public ResponseEntity hello(@AuthenticationPrincipal Jwt jwt) {
            log.info("JWT (@AuthenticationPrincipal): {}", jwt.getSubject());

            JwtAuthenticationToken token =
                    (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            log.info("JWT (SecurityContextHolder): {}", token.getToken().getSubject());

            return ResponseEntity.ok(String.format("hello there %s", token.getToken().getSubject()));
        }
    }
}
