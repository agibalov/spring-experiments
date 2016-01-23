package me.loki2302;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Config {
    @Bean
    AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder) {
        return builder.getOrBuild();
    }

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
