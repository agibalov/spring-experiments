package me.loki2302.impl;

import me.loki2302.impl.BlogService;
import me.loki2302.impl.entities.User;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = User.class)
public class AppConfig {
    @Bean
    BlogService blogService() {
        return new BlogService();
    }
}
