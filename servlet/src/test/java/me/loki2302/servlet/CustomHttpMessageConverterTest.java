package me.loki2302.servlet;

import me.loki2302.servlet.shared.YamlHttpMessageConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class CustomHttpMessageConverterTest {
    @Test
    public void getAsString() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.setMessageConverters(Collections.singletonList(new StringHttpMessageConverter()));
        String result = restTemplate.getForObject("http://localhost:8080/", String.class);
        assertEquals("message: hello there\n", result);
    }

    @Test
    public void getAsObject() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.setMessageConverters(Collections.singletonList(new YamlHttpMessageConverter(new Yaml())));
        DummyDto result = restTemplate.getForObject("http://localhost:8080/", DummyDto.class);
        assertEquals("hello there", result.message);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config implements WebMvcConfigurer {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new YamlHttpMessageConverter(new Yaml()));
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public DummyDto index() {
            DummyDto dummyDto = new DummyDto();
            dummyDto.message = "hello there";
            return dummyDto;
        }
    }

    public static class DummyDto {
        public String message;
    }
}
