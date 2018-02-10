package me.loki2302.servlet.dummysc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Inspired by https://github.com/awslabs/aws-serverless-java-container/tree/master/aws-serverless-java-container-spring
 * The goal is to see what it looks like to build my own servlet container adapter
 * (for example, to make Spring MVC work with AWS Lambda proxy integration)
 * Briefly: there's A LOT of code to write + one should know Servlet APIs
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class DummyServletContainerTest {
    @Autowired
    private ServletContext servletContext;

    @Test
    public void dummyServletContainerShouldWork() {
        DummyServletContext dummyServletContext = (DummyServletContext)servletContext;
        String responseBody = dummyServletContext.test();
        assertEquals("{\"message\":\"hi there\"}", responseBody);
    }

    @SpringBootApplication
    public static class Config {
        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new DummyEmbeddedServletContainerFactory();
        }

        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }

    @RestController
    public static class DummyController {
        @GetMapping("/")
        public Map<String, String> index() {
            return Collections.singletonMap("message", "hi there");
        }
    }

    public static class DummyEmbeddedServletContainerFactory implements ServletWebServerFactory {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyEmbeddedServletContainerFactory.class);

        @Override
        public WebServer getWebServer(ServletContextInitializer... initializers) {
            LOGGER.info("getEmbeddedServletContainer(), initializers={}", initializers.length);

            DummyServletContext servletContext = new DummyServletContext();
            for(ServletContextInitializer initializer : initializers) {
                try {
                    initializer.onStartup(servletContext);
                } catch (ServletException e) {
                    LOGGER.error("Error while calling onStartup() for {}", initializer, e);
                }
            }

            try {
                for (Map.Entry<String, Filter> filterEntry : servletContext.filters.entrySet()) {
                    String filterName = filterEntry.getKey();
                    Filter filter = filterEntry.getValue();
                    filter.init(new DummyFilterConfig(filterName, servletContext));
                }
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }

            try {
                for (Map.Entry<String, Servlet> servletEntry : servletContext.servlets.entrySet()) {
                    String servletName = servletEntry.getKey();
                    Servlet servlet = servletEntry.getValue();
                    servlet.init(new DummyServletConfig(servletName, servletContext));
                }
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }

            return new DummyEmbeddedServletContainer();
        }
    }

    public static class DummyEmbeddedServletContainer implements WebServer {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyEmbeddedServletContainer.class);

        @Override
        public void start() throws WebServerException {
            LOGGER.info("start()");
        }

        @Override
        public void stop() throws WebServerException {
            LOGGER.info("stop()");
        }

        @Override
        public int getPort() {
            LOGGER.info("getPort()");
            return -1;
        }
    }
}
