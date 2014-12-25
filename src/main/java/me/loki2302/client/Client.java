package me.loki2302.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Client {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
