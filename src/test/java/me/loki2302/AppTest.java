package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AppConfiguration.class)
@WebAppConfiguration
@IntegrationTest
public class AppTest {
    @Autowired
    private EmbeddedWebApplicationContext server;

    @Test
    public void dummy() {
        String uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(server.getEmbeddedServletContainer().getPort())
                .path("/")
                .build().toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String message = restTemplate.getForObject(uri, String.class);
        assertEquals("hello", message);
    }
}
