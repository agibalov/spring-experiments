package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebIntegrationTest
@SpringApplicationConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
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
    public static class Config extends WebMvcConfigurerAdapter {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new YamlHttpMessageConverter(new Yaml()));
        }
    }

    public static class YamlHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
        private final Yaml yaml;

        public YamlHttpMessageConverter(Yaml yaml) {
            super(new MediaType("application", "yaml"));
            this.yaml = yaml;
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return true;
        }

        @Override
        protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            return yaml.loadAs(inputMessage.getBody(), clazz);
        }

        @Override
        protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            String yamlString = yaml.dumpAs(o, Tag.MAP, DumperOptions.FlowStyle.BLOCK);

            try(PrintWriter printWriter = new PrintWriter(outputMessage.getBody())) {
                printWriter.print(yamlString);
            }
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
