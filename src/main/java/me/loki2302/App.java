package me.loki2302;

import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
@EnableConfigurationProperties
public class App {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplicationBuilder()
                .sources(App.class)
                .banner(new ResourceBanner(new ClassPathResource("/banner.txt")))
                .build();

        app.run(args);
    }
}
