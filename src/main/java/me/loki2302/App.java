package me.loki2302;

import org.springframework.boot.SpringApplication;

public class App {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AppConfiguration.class);
        application.run(args);
    }
}
