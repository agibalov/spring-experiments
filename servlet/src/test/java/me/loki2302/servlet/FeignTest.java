package me.loki2302.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.Assert.assertEquals;

/**
 * How do I configure a smarter Content-Type handling - JSON vs. plain text?
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class FeignTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void ping() {
        ApiClient apiClient = Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .target(ApiClient.class, "http://localhost:8080");
        DummyDTO result = apiClient.doSomething();
        assertEquals("hello there", result.message);
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
        public DummyDTO index() {
            DummyDTO dummyDTO = new DummyDTO();
            dummyDTO.message = "hello there";
            return dummyDTO;
        }
    }

    public interface ApiClient {
        @Headers("Content-Type: application/json")
        @RequestLine("GET /")
        DummyDTO doSomething();
    }

    public static class DummyDTO {
        public String message;
    }
}
