package io.agibalov;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.apache.catalina.AccessLog;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.io.IOException;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Component
    public class TomcatLoggingCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        @Override
        public void customize(TomcatServletWebServerFactory factory) {
            LogbackValve logbackValve = new LogbackValve();
            logbackValve.setFilename("tomcat-logging.xml");
            factory.addEngineValves(logbackValve);

            factory.addEngineValves(new MyAccessLogValve());
        }
    }

    public static class MyAccessLogValve extends ValveBase implements AccessLog {
        private final static Logger LOGGER = LoggerFactory.getLogger(MyAccessLogValve.class);

        @Override
        public void log(Request request, Response response, long time) {
            LOGGER.info("I am a valve! {}", request.getRequestURI());
        }

        @Override
        public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        }

        @Override
        public boolean getRequestAttributesEnabled() {
            return false;
        }

        @Override
        public void invoke(Request request, Response response) throws IOException, ServletException {
            getNext().invoke(request, response);
        }
    }

    @RestController
    public static class DummyController {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyController.class);

        @GetMapping("/")
        public String index() {
            LOGGER.info("I am application log");
            return "hello";
        }
    }
}
