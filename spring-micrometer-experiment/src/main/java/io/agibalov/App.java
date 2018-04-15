package io.agibalov;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;

@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    @Timed // looks like @Timed only works for controllers
    public static class DummyController {
        @GetMapping("/")
        @Timed(value = "hello", percentiles = { 0.5, 0.95 })
        public ResponseEntity<?> hello() {
            return ResponseEntity.ok(Collections.singletonMap(
                    "message", String.format("Hello %s", Instant.now())));
        }
    }

    @Component
    public static class SomeService {
        private final static Logger LOGGER = LoggerFactory.getLogger(SomeService.class);
        private final Timer doSomethingCallsTimer;
        private final Timer doSomethingElseCallsTimer;

        public SomeService(MeterRegistry meterRegistry) {
            this.doSomethingCallsTimer = Timer.builder("do-something-calls")
                    .publishPercentiles(0.5, 0.95)
                    .register(meterRegistry);

            this.doSomethingElseCallsTimer = Timer.builder("do-something-else-calls")
                    .publishPercentiles(0.5, 0.95)
                    .register(meterRegistry);
        }

        @Scheduled(cron = "* * * * * *")
        public void doSomething() {
            doSomethingCallsTimer.record(() -> {
                LOGGER.info("Ping!");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Scheduled(cron = "*/4 * * * * *")
        public void doSomethingElse() {
            doSomethingElseCallsTimer.record(() -> {
                LOGGER.info("Other ping!");
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
