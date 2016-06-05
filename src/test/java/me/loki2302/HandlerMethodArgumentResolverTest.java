package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebIntegrationTest
@SpringApplicationConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class HandlerMethodArgumentResolverTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();
        assertEquals("hello there 1", restTemplate.getForObject("http://localhost:8080/", String.class));
        assertEquals("hello there 2", restTemplate.getForObject("http://localhost:8080/", String.class));
        assertEquals("hello there 3", restTemplate.getForObject("http://localhost:8080/", String.class));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config extends WebMvcConfigurerAdapter {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new RequestCountHandlerMethodArgumentResolver());
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String index(@RequestCount long rc) {
            return "hello there " + rc;
        }
    }

    public static class RequestCountHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
        private long requestCount;

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(RequestCount.class) &&
                    (parameter.getParameterType().equals(Long.class) || parameter.getParameterType().equals(long.class));
        }

        @Override
        public Object resolveArgument(
                MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory) throws Exception {

            if(!supportsParameter(parameter)) {
                return null;
            }

            ++requestCount;

            return requestCount;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequestCount {
    }
}
