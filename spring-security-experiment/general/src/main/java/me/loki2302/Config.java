package me.loki2302;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Config {
    @Bean
    public Calculator calculator() {
        return new Calculator();
    }

    @Bean
    public DivSecurityDecider divSecurityDecider() {
        return new DivSecurityDecider();
    }

    @Bean
    public NoteService noteService() {
        return new NoteService();
    }
}
