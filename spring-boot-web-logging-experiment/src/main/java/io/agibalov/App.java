package io.agibalov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class DummyController {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyController.class);

        @GetMapping("/")
        public String index() {
            LOGGER.info("I am application log");
            return "hello";
        }
    }
}
