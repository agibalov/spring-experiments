package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "custom.mapping.path=/omg/wtf/bbq/")
@RunWith(SpringRunner.class)
public class ConfigurableControllerMappingPathViaPropertiesTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();
        assertEquals("hello there", restTemplate.getForObject("http://localhost:8080/omg/wtf/bbq/something", String.class));
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
    @RequestMapping("${custom.mapping.path}")
    public static class DummyController {
        @RequestMapping(method = RequestMethod.GET, value = "/something")
        public String index() {
            return "hello there";
        }
    }
}
