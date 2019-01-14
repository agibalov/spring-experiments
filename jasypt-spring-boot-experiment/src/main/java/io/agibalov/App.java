package io.agibalov;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@SpringBootApplication
@EnableEncryptableProperties
@EnableConfigurationProperties(AppProperties.class)
@PropertySource(value = "file:${user.home}/.the-app/secrets.yaml", ignoreResourceNotFound = true)
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Value("${login}")
    private String login;

    @Value("${password}")
    private String password;

    @Autowired
    private AppProperties appProperties;

    @Override
    public void run(String... args) {
        log.info("login is {}", login);
        log.info("password is {}", password);
        log.info("app properties: {}", appProperties);
    }
}
