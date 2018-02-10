package me.loki2302.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class RestTemplateLoggingTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(
                        new HttpComponentsClientHttpRequestFactory()));
        restTemplate.setInterceptors(Collections.singletonList(
                new LoggingClientHttpRequestInterceptor()));

        String result = restTemplate.getForObject("http://localhost:8080/", String.class);
        assertEquals("hello there", result);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String index() {
            return "hello there";
        }
    }

    public static class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        private final static Logger LOGGER = LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            String requestBodyString = "<empty>";
            if(body != null && body.length > 0) {
                requestBodyString = new String(body, StandardCharsets.UTF_8);
            }

            LOGGER.info("Request: method={} uri={} body={}", request.getMethod(), request.getURI(), requestBodyString);

            ClientHttpResponse response = execution.execute(request, body);
            String responseBodyString = "<empty>";
            try(InputStream is = response.getBody()) {
                responseBodyString = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            }

            LOGGER.info("Response: status={} body={}", response.getStatusCode(), responseBodyString);

            return response;
        }
    }
}
