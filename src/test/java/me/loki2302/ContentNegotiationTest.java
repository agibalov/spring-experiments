package me.loki2302;

import me.loki2302.shared.YamlHttpMessageConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ContentNegotiationTest {
    @Test
    public void canGetContentAsJson() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.setMessageConverters(Collections.singletonList(new StringHttpMessageConverter()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080", HttpMethod.GET, httpEntity, String.class);
        assertEquals("{\"message\":\"hello there\"}", responseEntity.getBody());
    }

    @Test
    public void canGetContentAsYaml() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.setMessageConverters(Collections.singletonList(new StringHttpMessageConverter()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(new MediaType("application", "yaml")));
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080", HttpMethod.GET, httpEntity, String.class);
        assertEquals("message: hello there\n", responseEntity.getBody());
    }

    @Test
    public void canGet406WhenRequestingOmg() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.setMessageConverters(Collections.singletonList(new StringHttpMessageConverter()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(new MediaType("application", "omg")));
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        try {
            restTemplate.exchange("http://localhost:8080", HttpMethod.GET, httpEntity, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_ACCEPTABLE, e.getStatusCode());
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config extends WebMvcConfigurerAdapter {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new YamlHttpMessageConverter(new Yaml()));
            converters.add(new MappingJackson2HttpMessageConverter());
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
