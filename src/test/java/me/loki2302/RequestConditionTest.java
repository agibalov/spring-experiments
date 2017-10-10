package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class RequestConditionTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();
        assertEquals("DEFAULT handler", restTemplate.getForObject("http://localhost:8080/", String.class));
        assertEquals("DEFAULT handler", restTemplate.getForObject("http://localhost:8080/?qwerty", String.class));
        assertEquals("A handler", restTemplate.getForObject("http://localhost:8080/?a", String.class));
        assertEquals("B handler", restTemplate.getForObject("http://localhost:8080/?b", String.class));

        try {
            restTemplate.getForObject("http://localhost:8080/?a&b", String.class);
            fail();
        } catch (HttpServerErrorException e) {
            // ambiguous mapping
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
        }

        try {
            restTemplate.getForObject("http://localhost:8080/?b&a", String.class);
            fail();
        } catch (HttpServerErrorException e) {
            // ambiguous mapping
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Bean
        public MyWebMvcRegistrationsAdapter myWebMvcRegistrationsAdapter() {
            return new MyWebMvcRegistrationsAdapter();
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping("/")
        @ParamPresenceMapping("a")
        public String helloA() {
            return "A handler";
        }

        @RequestMapping("/")
        @ParamPresenceMapping("b")
        public String helloB() {
            return "B handler";
        }

        @RequestMapping("/")
        public String helloDefault() {
            return "DEFAULT handler";
        }
    }

    public static class MyWebMvcRegistrationsAdapter implements WebMvcRegistrations {
        @Override
        public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
            return new DummyRequestMappingHandlerMapping();
        }
    }

    public static class DummyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
            ParamPresenceMapping paramPresenceMappingAnnotation = AnnotationUtils.findAnnotation(
                    handlerType,
                    ParamPresenceMapping.class);

            return createCondition(paramPresenceMappingAnnotation);
        }

        @Override
        protected RequestCondition<?> getCustomMethodCondition(Method method) {
            ParamPresenceMapping paramPresenceMappingAnnotation = AnnotationUtils.findAnnotation(
                    method,
                    ParamPresenceMapping.class);

            return createCondition(paramPresenceMappingAnnotation);
        }

        private RequestCondition<?> createCondition(ParamPresenceMapping paramPresenceMappingAnnotation) {
            if(paramPresenceMappingAnnotation == null) {
                return null;
            }

            return new ParamPresenceRequestCondition(Collections.singleton(paramPresenceMappingAnnotation.value()));
        }
    }

    public static class ParamPresenceRequestCondition implements RequestCondition<ParamPresenceRequestCondition> {
        private final Set<String> requiredParameters = new HashSet<>();

        public ParamPresenceRequestCondition(Set<String> requiredParameters) {
            this.requiredParameters.addAll(requiredParameters);
        }

        @Override
        public ParamPresenceRequestCondition combine(ParamPresenceRequestCondition other) {
            Set<String> params = new HashSet<>(this.requiredParameters);
            params.addAll(other.requiredParameters);
            return new ParamPresenceRequestCondition(params);
        }

        @Override
        public ParamPresenceRequestCondition getMatchingCondition(HttpServletRequest request) {
            Set<String> namesOfParametersInThisRequest = new HashSet<>(request.getParameterMap().keySet());
            namesOfParametersInThisRequest.retainAll(requiredParameters);
            if(!namesOfParametersInThisRequest.isEmpty()) {
                return this;
            }

            return null;
        }

        @Override
        public int compareTo(ParamPresenceRequestCondition other, HttpServletRequest request) {
            return other.requiredParameters.size() - requiredParameters.size();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    public @interface ParamPresenceMapping {
        String value();
    }
}
