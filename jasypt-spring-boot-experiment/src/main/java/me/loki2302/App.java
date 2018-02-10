package me.loki2302;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("username is {}", username);
        LOGGER.info("password is {}", password);
    }
}
