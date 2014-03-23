package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class App {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AppConfiguration.class);

        ConfigurableApplicationContext context = null;
        try {
            context = application.run(args);
        } finally {
            if(context != null) {
                context.close();
            }
        }
    }
}
