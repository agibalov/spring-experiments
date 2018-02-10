package me.loki2302;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

/**
 * There's a configuration server (CS) at http://localhost:9090
 * There's an application (APP) at http://localhost:8080
 *
 * CS exposes a configuration at /
 * The APP is supposed to use that configuration. When the APP is started, it loads the configuration once.
 * To force configuration reload, use the APP's GET /refresh endpoint.
 * The APP exposes a GET /?name=John endpoint that provides a greeting message based on
 * greeting template loaded from CS.
 *
 * Sure, the APP can automatically reload the updated configuration if contextRefresher is called every N minutes.
 */
public class DummyTest {
    @Test
    public void dummy() throws JsonProcessingException {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(9090));
        wireMockServer.start();
        try {
            wireMockServer.stubFor(get("/").willReturn(aResponse()
                    .withHeader("Content-type", "application/json")
                    .withBody(new ObjectMapper().writeValueAsBytes(new HashMap<String, String>() {{
                        put("greetingTemplate", "Hello %s!");
                    }}))));
            ConfigurableApplicationContext context = new SpringApplicationBuilder()
                    .sources(App.class)
                    .run("--my.cloud.config.url=http://localhost:9090/");
            try {
                RestTemplate restTemplate = new RestTemplate();

                {
                    String greeting = restTemplate.getForObject(
                            "http://localhost:8080/?name=Andrey",
                            String.class);
                    assertEquals("Hello Andrey!", greeting);
                }

                wireMockServer.stubFor(get("/").willReturn(aResponse()
                        .withHeader("Content-type", "application/json")
                        .withBody(new ObjectMapper().writeValueAsBytes(new HashMap<String, String>() {{
                            put("greetingTemplate", "Hey %s!!!");
                        }}))));

                {
                    restTemplate.getForObject("http://localhost:8080/refresh", String.class);

                    String greeting = restTemplate.getForObject(
                            "http://localhost:8080/?name=Andrey",
                            String.class);
                    assertEquals("Hey Andrey!!!", greeting);
                }
            } finally {
                context.close();
            }
        } finally {
            wireMockServer.stop();
        }
    }

    @RefreshScope
    @RestController
    public static class DummyController {
        @Value("${greetingTemplate}")
        private String greetingTemplate;

        @Autowired
        private ContextRefresher contextRefresher;

        @GetMapping("/")
        public String index(@RequestParam("name") String name) {
            return String.format(greetingTemplate, name);
        }

        @GetMapping("/refresh")
        public String refresh() {
            contextRefresher.refresh();
            return "ok";
        }
    }

    @SpringBootApplication
    public static class App {
    }

    @Configuration
    @EnableConfigurationProperties(MyCloudConfigProperties.class)
    public static class MyCloudConfigBootstrapConfiguration {
        @Bean
        public MyCloudConfigProperties myPropertySourceLocatorProperties() {
            return new MyCloudConfigProperties();
        }

        @Bean
        public MyCloudPropertySourceLocator myPropertySourceLocator() {
            MyCloudConfigProperties myCloudConfigProperties =
                    myPropertySourceLocatorProperties();
            String configUrl = myCloudConfigProperties.getUrl();
            return new MyCloudPropertySourceLocator(configUrl);
        }
    }

    public static class MyCloudPropertySourceLocator implements PropertySourceLocator {
        private String configUrl;

        public MyCloudPropertySourceLocator(String configUrl) {
            this.configUrl = configUrl;
        }

        @Override
        public PropertySource<?> locate(Environment environment) {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> remoteProperties = restTemplate.getForObject(configUrl, Map.class);

            MapPropertySource mapPropertySource = new MapPropertySource(
                    "myCloudPropertySource",
                    remoteProperties);
            return mapPropertySource;
        }
    }

    @ConfigurationProperties("my.cloud.config")
    public static class MyCloudConfigProperties {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
