package me.loki2302.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class RateLimitingHandlerInterceptorTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(!response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                    super.handleError(response);
                } else {
                    throw new RateLimitExceededException();
                }
            }
        });

        makeRequest(restTemplate, "loki2302");
        makeRequest(restTemplate, "loki2302");
        makeRequest(restTemplate, "loki2302");

        try {
            makeRequest(restTemplate, "loki2302");
            fail();
        } catch (RateLimitExceededException e) {
        }

        makeRequest(restTemplate, "user1");
        makeRequest(restTemplate, "user1");
        makeRequest(restTemplate, "user1");

        try {
            makeRequest(restTemplate, "user1");
            fail();
        } catch (RateLimitExceededException e) {
        }
    }

    private static void makeRequest(RestTemplate restTemplate, String username) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-USERNAME", username);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create("http://localhost:8080"));
        restTemplate.exchange(requestEntity, String.class);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config extends WebMvcConfigurerAdapter {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new RateLimitingHandlerInterceptor());
        }

        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Bean
        public ErrorHandlingControllerAdvice errorHandlingControllerAdvice() {
            return new ErrorHandlingControllerAdvice();
        }
    }

    public static class RateLimitingHandlerInterceptor extends HandlerInterceptorAdapter {
        private final Map<String, Long> requestCountByUsername = new HashMap<>();

        @Override
        public boolean preHandle(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler) throws Exception {

            String username = request.getHeader("X-USERNAME");
            if(username == null || username.isEmpty()) {
                return true;
            }

            synchronized (requestCountByUsername) {
                Long requestCount = requestCountByUsername.get(username);
                if(requestCount == null) {
                    requestCount = Long.valueOf(0);
                }

                try {
                    if (requestCount == 3) {
                        throw new RateLimitExceededException();
                    }
                } finally {
                    ++requestCount;

                    requestCountByUsername.put(username, requestCount);
                }
            }

            return true;
        }
    }

    @ControllerAdvice
    public static class ErrorHandlingControllerAdvice {
        @ExceptionHandler(RateLimitExceededException.class)
        public ResponseEntity handleRateLimitExceededException(RateLimitExceededException e) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new RateLimitExceededErrorDto());
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String index() {
            return "hi there";
        }
    }

    public static class RateLimitExceededErrorDto {
        public String message = "Rate limit exceeded";
    }

    public static class RateLimitExceededException extends RuntimeException {}
}
