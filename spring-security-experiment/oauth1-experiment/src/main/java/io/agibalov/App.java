package io.agibalov;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth.provider.*;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.nonce.NullNonceServices;
import org.springframework.security.oauth.provider.nonce.OAuthNonceServices;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EnableWebSecurity
    public static class ResourceServerConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private ProtectedResourceProcessingFilter protectedResourceProcessingFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .mvcMatcher("/api/**")
                    .authorizeRequests()
                        .mvcMatchers("/api/**").authenticated()
                        .and()
                    .addFilterBefore(protectedResourceProcessingFilter, AnonymousAuthenticationFilter.class);
        }
    }

    @Bean
    public OAuthProcessingFilterEntryPoint oauthAuthenticationEntryPoint() {
        return new OAuthProcessingFilterEntryPoint();
    }

    @Bean
    public ConsumerDetailsService consumerDetailsService() {
        return new DummyConsumerDetailsService(Arrays.asList(
                KnownConsumer.builder()
                        .consumerKey("aaa123")
                        .consumerSecret("thesecret")
                        .build()));
    }

    @Bean
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        return new InMemoryProviderTokenServices();
    }

    @Bean
    public OAuthAuthenticationHandler oAuthAuthenticationHandler() {
        return (request, authentication, authToken) -> authentication;
    }

    @Bean
    public OAuthNonceServices oAuthNonceServices() {
        return new NullNonceServices();
    }

    @Bean
    public ProtectedResourceProcessingFilter protectedResourceProcessingFilter(
            ConsumerDetailsService consumerDetailsService,
            OAuthNonceServices oAuthNonceServices,
            OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint,
            OAuthAuthenticationHandler oAuthAuthenticationHandler,
            OAuthProviderTokenServices oAuthProviderTokenServices) {

        ProtectedResourceProcessingFilter filter = new ProtectedResourceProcessingFilter();
        filter.setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
        filter.setAuthHandler(oAuthAuthenticationHandler);
        filter.setConsumerDetailsService(consumerDetailsService);
        filter.setNonceServices(oAuthNonceServices);
        filter.setTokenServices(oAuthProviderTokenServices);
        filter.setSignatureMethodFactory(new HmacSha256OAuthSignatureMethodFactory());
        return filter;
    }

    @Data
    @Builder
    public static class KnownConsumer {
        private String consumerKey;
        private String consumerSecret;
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
        public ResponseEntity hello(ConsumerAuthentication consumerAuthentication) {
            String consumerKey = consumerAuthentication.getConsumerDetails().getConsumerKey();
            log.info("Authentication: {}", consumerKey);
            return ResponseEntity.ok(String.format("hello world %s", consumerKey));
        }
    }
}
