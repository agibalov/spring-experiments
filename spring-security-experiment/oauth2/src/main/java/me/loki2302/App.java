package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class HomeController {
        @GetMapping("/")
        public Map<String, String> hello(Principal principal) throws IOException {
            String responseText;
            if(principal == null) {
                responseText = String.format("PRINCIPAL is null");
            } else if(principal instanceof OAuth2Authentication) {
                OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)principal;
                boolean isClient = oAuth2Authentication.isClientOnly();
                if(isClient) {
                    String clientId = (String)oAuth2Authentication.getPrincipal();
                    responseText = String.format("PRINCIPAL is client '%s' (%s)", clientId, oAuth2Authentication.getAuthorities());
                } else {
                    String username = (String)oAuth2Authentication.getPrincipal();
                    String clientId = oAuth2Authentication.getOAuth2Request().getClientId();
                    responseText = String.format("PRINCIPAL is user '%s' (%s) [clientId=%s]",
                            username, oAuth2Authentication.getAuthorities(), clientId);
                }
            } else {
                responseText = String.format("PRINCIPAL is %s???", principal.getClass());
            }

            return Collections.singletonMap("message", responseText);
        }

        @PreAuthorize("#oauth2.isUser() && #oauth2.hasScope('cats')")
        @GetMapping("/cats")
        public String readCats() {
            return "meow";
        }
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            // this disables sessions completely,
            // JSESSIONID is never sent
            servletContext.setSessionTrackingModes(new HashSet<>());
        };
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            return new OAuth2MethodSecurityExpressionHandler();
        }
    }

    @Configuration
    @EnableWebSecurity
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(User.builder()
                    .passwordEncoder(raw -> passwordEncoder().encode(raw))
                    .username("user1")
                    .password("user1password")
                    .roles("USER")
                    .build());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(jwtAccessTokenConverter());
        }

        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
            jwtAccessTokenConverter.setSigningKey("mySecretSigningKey");
            return jwtAccessTokenConverter;
        }
    }

    @Configuration
    @EnableAuthorizationServer
    public static class OAuth2AuthorizationServiceConfig extends AuthorizationServerConfigurerAdapter {
        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private TokenStore tokenStore;

        @Autowired
        private AccessTokenConverter accessTokenConverter;

        @Autowired
        private UserDetailsService userDetailsService;

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) {
            security.passwordEncoder(passwordEncoder);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory()
                    .withClient("MyClientId1")
                    .accessTokenValiditySeconds(10)
                    .secret(passwordEncoder.encode("MyClientId1Secret"))
                    .authorizedGrantTypes("client_credentials", "password", "refresh_token", "authorization_code")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "cats", "beer")

                    .and()

                    .withClient("MyClientId2")
                    .accessTokenValiditySeconds(3)
                    .secret(passwordEncoder.encode("MyClientId2Secret"))
                    .authorizedGrantTypes("client_credentials", "password", "refresh_token", "authorization_code")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "cats", "beer");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints
                    .tokenStore(tokenStore)
                    .accessTokenConverter(accessTokenConverter)
                    .userDetailsService(userDetailsService)
                    .authenticationManager(authenticationManager);
        }
    }

    @Configuration
    @EnableResourceServer
    public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            // remove this to disallow requests without authentication
            http.authorizeRequests().anyRequest().permitAll();
        }
    }
}
