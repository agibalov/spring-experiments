package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ResponseHeaderTest {
    @Test
    public void noContentEndPointShouldRespondWithHeader() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.getForEntity("http://localhost:8080/no-content", Void.class);
        assertEquals("Hello there", responseEntity.getHeaders().get("TestHeader").get(0));
    }

    @Test
    public void yesContentEndPointShouldRespondWithHeader() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.getForEntity("http://localhost:8080/yes-content", Void.class);
        assertEquals("Hello there", responseEntity.getHeaders().get("TestHeader").get(0));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Bean
        public AddResponseHeaderFilter addResponseHeaderFilter() {
            return new AddResponseHeaderFilter();
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping("/no-content")
        public ResponseEntity helloNoContent() {
            return ResponseEntity.noContent().build();
        }

        @RequestMapping("/yes-content")
        public ResponseEntity helloYesContent() {
            return ResponseEntity.ok("hi");
        }
    }

    public static class AddResponseHeaderFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {

            ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);
            filterChain.doFilter(request, contentCachingResponseWrapper);
            contentCachingResponseWrapper.setHeader("TestHeader", "Hello there");
            contentCachingResponseWrapper.copyBodyToResponse();
        }
    }
}
