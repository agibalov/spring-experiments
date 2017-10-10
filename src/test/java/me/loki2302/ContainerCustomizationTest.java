package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ContainerCustomizationTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:8080/", String.class);
        assertTrue(result.getHeaders().containsKey("Set-Cookie"));
        assertTrue(result.getHeaders().get("Set-Cookie").get(0).contains("MYCUSTOMSESSIONCOOKIE"));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Bean
        public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
            TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
            tomcatServletWebServerFactory.addContextCustomizers((TomcatContextCustomizer) context ->
                    context.setSessionCookieName("MYCUSTOMSESSIONCOOKIE"));
            return tomcatServletWebServerFactory;
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String index(HttpSession session) { // inject session to start it
            return "hello there";
        }
    }
}
