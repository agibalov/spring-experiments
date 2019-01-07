package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class AppController {
        private final static Logger log = LoggerFactory.getLogger(AppController.class);

        private Object updateLock = new Object();
        private UpdateDTO update;

        @RequestMapping(value = "/status", method = RequestMethod.GET)
        public UpdateDTO index() {
            log.info("Update has been requested");

            synchronized (updateLock) {
                return update;
            }
        }

        @RequestMapping(value = "/status", method = RequestMethod.PUT)
        public void update(@RequestBody UpdateDTO update) {
            synchronized (updateLock) {
                log.info("Someone has sent an update: {}", update.sum);
                this.update = update;
            }
        }
    }

    public static class UpdateDTO {
        public Integer sum;
    }
}
