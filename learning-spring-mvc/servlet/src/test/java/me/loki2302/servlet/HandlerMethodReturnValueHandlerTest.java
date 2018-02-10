package me.loki2302.servlet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class HandlerMethodReturnValueHandlerTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity result = restTemplate.getForEntity("http://localhost:8080/2", String.class);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());

        try {
            restTemplate.getForEntity("http://localhost:8080/1", String.class);
        } catch(HttpClientErrorException e) {
            Assert.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config extends WebMvcConfigurerAdapter {
        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
            returnValueHandlers.add(new MyHandlerMethodReturnValueHandler());
        }

        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }

    public static class MyHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return Result.class.isAssignableFrom(returnType.getParameterType());
        }

        @Override
        public void handleReturnValue(
                Object returnValue,
                MethodParameter returnType,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest) throws Exception {

            HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
            ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
            servletServerHttpResponse.setStatusCode(((Result)returnValue).httpStatus);
            mavContainer.setRequestHandled(true);
        }
    }

    public static abstract class Result {
        protected final HttpStatus httpStatus;

        protected Result(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        public static OkResult ok() {
            return new OkResult();
        }

        public static NotFoundResult notFoundResult() {
            return new NotFoundResult();
        }

        public static class OkResult extends Result {
            public OkResult() {
                super(HttpStatus.OK);
            }
        }
        public static class NotFoundResult extends Result {
            public NotFoundResult() {
                super(HttpStatus.NOT_FOUND);
            }
        }
    }

    @Controller // NOTE: @Controller, not @RestController
    public static class DummyController {
        @RequestMapping(value = "/{id}", method = RequestMethod.GET)
        public Result index(@PathVariable("id") long id) {
            if(id % 2 == 0) {
                return Result.ok();
            }

            return Result.notFoundResult();
        }
    }
}
