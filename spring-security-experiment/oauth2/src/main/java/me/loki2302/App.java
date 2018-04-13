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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
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
                Object innerPrincipal = oAuth2Authentication.getPrincipal();
                if(innerPrincipal instanceof String) {
                    String clientId = (String)innerPrincipal;
                    responseText = String.format("PRINCIPAL is client '%s' (%s)", clientId, oAuth2Authentication.getAuthorities());
                } else if(innerPrincipal instanceof User) {
                    String clientId = oAuth2Authentication.getOAuth2Request().getClientId();
                    User user = (User)innerPrincipal;
                    responseText = String.format("PRINCIPAL is user '%s' (%s) [clientId=%s]",
                            user.getUsername(), oAuth2Authentication.getAuthorities(), clientId);
                } else {
                    responseText = String.format("PRINCIPAL is OAuth2??? (%s)", oAuth2Authentication.getAuthorities());
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
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .inMemoryAuthentication()
                    .passwordEncoder(NoOpPasswordEncoder.getInstance())
                    .withUser("user1")
                    .password("user1password")
                    .roles("USER");
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Configuration
    @EnableAuthorizationServer
    public static class OAuth2AuthorizationServiceConfig extends AuthorizationServerConfigurerAdapter {
        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.passwordEncoder(NoOpPasswordEncoder.getInstance());
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory()
                    .withClient("MyClientId1")
                    .secret("MyClientId1Secret")
                    .authorizedGrantTypes("client_credentials", "password", "refresh_token", "authorization_code")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "cats", "beer");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    .tokenStore(tokenStore())
                    .authenticationManager(authenticationManager);
        }

        @Bean
        public TokenStore tokenStore() {
            return new InMemoryTokenStore();
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
