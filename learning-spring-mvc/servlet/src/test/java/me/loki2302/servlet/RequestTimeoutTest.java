package me.loki2302.servlet;

import lombok.extern.slf4j.Slf4j;
import me.loki2302.servlet.shared.SilentResponseErrorHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"spring.mvc.async.request-timeout=1000"}
)
@RunWith(SpringRunner.class)
public class RequestTimeoutTest {
    @Test
    public void canObserve500WhenRequestTakesTooLong() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new SilentResponseErrorHandler());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/timeout", String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("too long", responseEntity.getBody());
    }

    @Test
    public void canObserve200WhenRequestDoesNotTakeTooLong() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new SilentResponseErrorHandler());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/ok", String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("ok", responseEntity.getBody());
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }

    @Slf4j
    @RequestMapping
    public static class DummyController {
        @GetMapping("/timeout")
        public Callable<ResponseEntity<?>> timeout() {
            return () -> {
                for (int i = 0; i < 10; ++i) {
                    log.info("Sleep {}", i + 1);
                    Thread.sleep(500);
                }
                return ResponseEntity.ok("ok");
            };
        }

        @GetMapping("/ok")
        public Callable<ResponseEntity<?>> ok() {
            return () -> {
                for (int i = 0; i < 1; ++i) {
                    log.info("Sleep {}", i + 1);
                    Thread.sleep(500);
                }
                return ResponseEntity.ok("ok");
            };
        }

        @ExceptionHandler(AsyncRequestTimeoutException.class)
        public ResponseEntity<?> handleAsyncRequestTimeoutException() {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("too long");
        }
    }
}
