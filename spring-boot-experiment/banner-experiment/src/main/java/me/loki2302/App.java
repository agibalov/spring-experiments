package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class App {
    private final static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("*****************************************");
        runWithDefaultBannerConfig(args);

        LOGGER.info("*****************************************");
        runWithCustomBannerProvider(args);
    }

    private static void runWithDefaultBannerConfig(String[] args) {
        // uses the one at classpath:banner.txt
        SpringApplication.run(App.class, args);
    }

    private static void runWithCustomBannerProvider(String[] args) {
        // uses the custom one
        new SpringApplicationBuilder()
                .sources(App.class)
                .banner(new ResourceBanner(new ClassPathResource("/banner.txt")))
                .run(args);
    }
}
