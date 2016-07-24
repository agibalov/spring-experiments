package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ServletTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:8080/custom", String.class);
        assertEquals("hello there", result);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public ServletRegistrationBean servletRegistrationBean() {
            ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new DummyServlet());
            servletRegistrationBean.addUrlMappings("/custom/*");
            return servletRegistrationBean;
        }
    }

    public static class DummyServlet extends HttpServlet {
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try(PrintWriter printWriter = new PrintWriter(resp.getOutputStream())) {
                printWriter.printf("hello there");
            }
        }
    }
}
