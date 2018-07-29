package me.loki2302.servlet;

import lombok.extern.slf4j.Slf4j;
import me.loki2302.servlet.shared.SilentResponseErrorHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class ResponseBodyAdviceTest {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void itShouldSetTheHeaderWhenHandlerMethodSucceeds() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/", String.class);
        assertEquals(
                "Hello!",
                responseEntity.getHeaders().getFirst("TheHeaderThatGetsSetByMyResponseBodyAdvice"));
    }

    @Test
    public void itShouldSetTheHeaderWhenHandlerMethodThrows() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/error", String.class);
        assertEquals(
                "Hello!",
                responseEntity.getHeaders().getFirst("TheHeaderThatGetsSetByMyResponseBodyAdvice"));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Bean
        public MyResponseBodyAdvice myResponseBodyAdvice() {
            return new MyResponseBodyAdvice();
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new SilentResponseErrorHandler());
            return restTemplate;
        }
    }

    // ResponseBodyAdvice gets called after handler method (and after ExceptionHandler), but before
    // HttpMessageConverter, so it's the right place to enrich the response (body, headers, etc) before
    // it gets sent. Can be useful when one wants to set cache control headers globally.
    @ControllerAdvice
    @Slf4j
    public static class MyResponseBodyAdvice implements ResponseBodyAdvice<Object> {
        @Override
        public boolean supports(
                MethodParameter returnType,
                Class<? extends HttpMessageConverter<?>> converterType) {

            return ResponseEntity.class.isAssignableFrom(returnType.getParameterType());
        }

        @Override
        public Object beforeBodyWrite(
                Object body,
                MethodParameter returnType,
                MediaType selectedContentType,
                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                ServerHttpRequest request,
                ServerHttpResponse response) {

            log.info("Request: {}, setting header", request.getURI());
            response.getHeaders().set("TheHeaderThatGetsSetByMyResponseBodyAdvice", "Hello!");

            return body;
        }
    }

    @ResponseBody
    @RequestMapping
    @Slf4j
    public static class DummyController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public ResponseEntity<?> index() {
            return ResponseEntity.ok("hello world!");
        }

        @RequestMapping(value = "/error", method = RequestMethod.GET)
        public ResponseEntity<?> error() {
            throw new RuntimeException("I am an unexpected exception");
        }

        @ExceptionHandler(Throwable.class)
        public ResponseEntity<?> handleThrowable(Throwable throwable) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("errorMessage", throwable.getMessage()));
        }
    }
}
