package me.loki2302;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(App.class)
                .profiles("app")
                .run(args);

        // uncomment to make it shutdown once all CommandLineRunners are done
        // context.close();
    }
}
