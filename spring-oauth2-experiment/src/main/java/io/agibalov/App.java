package io.agibalov;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Configuration
    @Slf4j
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .authorizationRequestRepository(new CookieAuthorizationRequestRepository())
                    .and()
                    .successHandler((request, response, authentication) -> {
                        log.info("successHandler: {}", authentication);
                        // TODO: send redirect to /app/entrypoint/{JWT with facebook/google/github token here}
                        // TODO: the app is responsible for using that JWT to authenticate against this BE
                    })
                    .failureHandler((request, response, exception) -> {
                        log.info("failureHandler", exception);
                    });
        }

        /**
         * disables Servlet sessions
         */
        @Bean
        public ServletContextInitializer servletContextInitializer() {
            return servletContext -> servletContext.setSessionTrackingModes(new HashSet<>());
        }

        /**
         * they only provide one implementation of AuthorizationRequestRepository: HttpSessionOAuth2AuthorizationRequestRepository
         * It relies on Servlet sessions mechanism (JSESSIONID), which makes this authentication mechanism stateful
         * CookieAuthorizationRequestRepository is the implementation that relies on cookies instead of sessions
         */
        @Slf4j
        public static class CookieAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
            private final static String COOKIE_NAME = "authorizationRequest";
            private final static int COOKIE_TTL = 120;

            @SneakyThrows
            @Override
            public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
                Cookie cookie = Arrays.stream(request.getCookies())
                        .filter(c -> c.getName().equals(COOKIE_NAME))
                        .findFirst()
                        .orElse(null);
                if(cookie == null) {
                    return null;
                }

                return deserialize(cookie.getValue());
            }

            @SneakyThrows
            @Override
            public void saveAuthorizationRequest(
                    OAuth2AuthorizationRequest authorizationRequest,
                    HttpServletRequest request,
                    HttpServletResponse response) {

                if(authorizationRequest == null) {
                    Arrays.stream(request.getCookies())
                            .filter(c -> c.getName().equals(COOKIE_NAME))
                            .forEach(c -> {
                                c.setValue("");
                                c.setPath("/");
                                c.setMaxAge(0);
                                response.addCookie(c);
                            });
                    return;
                }

                Cookie cookie = new Cookie(COOKIE_NAME, serialize(authorizationRequest));
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(COOKIE_TTL);
                response.addCookie(cookie);
            }

            @Override
            public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
                return loadAuthorizationRequest(request);
            }

            @SneakyThrows
            private static String serialize(OAuth2AuthorizationRequest o) {
                return Base64.getEncoder().encodeToString(SerializationUtils.serialize(o));
            }

            @SneakyThrows
            private static OAuth2AuthorizationRequest deserialize(String s) {
                return (OAuth2AuthorizationRequest)SerializationUtils.deserialize(Base64.getDecoder().decode(s));
            }
        }
    }

    @RestController
    @Slf4j
    public static class DummyController {
        @GetMapping("/")
        public ResponseEntity<?> hello(Principal principal) {
            if(principal instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken)principal;
                log.info("oauth2: {}", oAuth2AuthenticationToken);
            } else {
                log.info("unknown: {}", principal);
            }

            return ResponseEntity.ok("hello there!");
        }
    }
}
