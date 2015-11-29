package me.loki2302.greetings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GreetingsConfiguration {
    private GreetingsConfigurer greetingsConfigurer;

    @Autowired(required = false)
    public void setConfigurer(GreetingsConfigurer greetingsConfigurer) {
        this.greetingsConfigurer = greetingsConfigurer;
    }

    @Bean
    public GreetingProvider greetingProvider() {
        GreetingProvider greetingProvider = new GreetingProvider();
        greetingProvider.setGreetingTemplate("Default, %s!"); // DEFAULT

        if(greetingsConfigurer != null) { // OPTIONAL CUSTOMIZATION
            greetingsConfigurer.configureGreetingProvider(greetingProvider);
        }

        return greetingProvider;
    }

    @Bean
    public GreetingService greetingService() {
        return new GreetingService();
    }
}
