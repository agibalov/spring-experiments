package me.loki2302.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
                .requestInterceptor(template ->
                        template.header("DummyRequestHeader","hello"))
                .errorDecoder(new ErrorDecoder.Default() {
                    @Override
                    public Exception decode(String methodKey, Response response) {
                        if(response.status() == HttpStatus.NOT_FOUND.value()) {
                            throw new MyCustomNotFoundException();
                        }

                        return super.decode(methodKey, response);
                    }
                })
                .logger(new Slf4jLogger("MyFeignLogger"))
                .logLevel(Logger.Level.FULL)
                .target(ApiClient.class, "http://localhost:8080");

        DummyDTO result = apiClient.doSomething();
        assertEquals("hello there", result.message);

        try {
            apiClient.doFourOFour();
            fail();
        } catch (MyCustomNotFoundException e) {
            // intentionally blank
        }
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

        @RequestLine("GET /definitely-four-o-four")
        void doFourOFour();
    }

    public static class DummyDTO {
        public String message;
    }

    public static class MyCustomNotFoundException extends RuntimeException {}
}
