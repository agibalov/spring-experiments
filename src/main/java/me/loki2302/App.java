package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);

        // GET http://localhost:8080/people to get a collection
        // POST http://localhost:8080/people to create a new one
    }

    @Configuration
    @EnableJpaRepositories
    @Import(RepositoryRestMvcConfiguration.class)
    @EnableAutoConfiguration
    public static class Config {
    }
}
