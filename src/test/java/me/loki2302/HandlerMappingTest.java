package me.loki2302;

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
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Map;

import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class HandlerMappingTest {
    @Autowired
    private Map<String, HandlerMapping> handlerMappings;

    @Test
    public void ping() {
        // @Controller, @RestController, etc
        RequestMappingHandlerMapping requestMappingHandlerMapping =
                (RequestMappingHandlerMapping)handlerMappings.get("requestMappingHandlerMapping");
        assertTrue(requestMappingHandlerMapping.getHandlerMethods().entrySet().stream().anyMatch(e -> {
            RequestMappingInfo requestMappingInfo = e.getKey();
            HandlerMethod handlerMethod = e.getValue();
            return requestMappingInfo.getPatternsCondition().getPatterns().contains("/somewhere/here") &&
                    requestMappingInfo.getMethodsCondition().getMethods().contains(RequestMethod.GET) &&
                    handlerMethod.getBean().equals("dummyController") &&
                    handlerMethod.getBeanType().equals(DummyController.class) &&
                    handlerMethod.getMethod().getName().equals("index");
        }));

        // Boot's favicon handler
        SimpleUrlHandlerMapping faviconHandlerMapping =
                (SimpleUrlHandlerMapping)handlerMappings.get("faviconHandlerMapping");
        assertTrue(faviconHandlerMapping.getUrlMap().entrySet().stream().anyMatch(e -> {
            return e.getKey().equals("**/favicon.ico") && e.getValue() instanceof ResourceHttpRequestHandler;
        }));
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
    @RequestMapping("/somewhere")
    public static class DummyController {
        @RequestMapping(value = "/here", method = RequestMethod.GET)
        public String index() {
            return "hello there";
        }
    }
}
