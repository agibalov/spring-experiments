package me.loki2302.servlet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class SessionScopeTest {
    @Test
    public void ping() {
        RestTemplate restTemplateA = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        RestTemplate restTemplateB = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        Assert.assertEquals("1", restTemplateA.getForObject("http://localhost:8080/", String.class));
        Assert.assertEquals("1", restTemplateB.getForObject("http://localhost:8080/", String.class));
        Assert.assertEquals("2", restTemplateA.getForObject("http://localhost:8080/", String.class));
        Assert.assertEquals("2", restTemplateB.getForObject("http://localhost:8080/", String.class));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
        public Counter counter() {
            return new Counter();
        }

        @Bean
        public CounterController counterController() {
            return new CounterController();
        }
    }

    @RestController
    public static class CounterController {
        @Autowired
        private Counter counter;

        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String index() {
            return String.valueOf(counter.increaseAndGet());
        }
    }

    public static class Counter {
        private int value;

        public int increaseAndGet() {
            return ++value;
        }
    }
}
