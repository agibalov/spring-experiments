package me.loki2302;

import me.loki2302.client.Client;
import me.loki2302.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Exchanger;

import static org.junit.Assert.assertEquals;

public class DummyTest {
    private ConfigurableApplicationContext serverContext;
    private ConfigurableApplicationContext clientContext;

    @Before
    public void start() throws InterruptedException {
        final Exchanger<ConfigurableApplicationContext> serverContextExchanger
                = new Exchanger<ConfigurableApplicationContext>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigurableApplicationContext serverContext = SpringApplication.run(Server.class);
                try {
                    serverContextExchanger.exchange(serverContext);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }).start();

        serverContext = serverContextExchanger.exchange(null);

        clientContext = new SpringApplicationBuilder(Client.class)
                .web(false).build().run();
    }

    @After
    public void stop() {
        clientContext.close();
        clientContext = null;

        serverContext.close();
        serverContext = null;
    }

    @Test
    public void canHave2Contexts() throws InterruptedException {
        RestTemplate restTemplate = clientContext.getBean(RestTemplate.class);
        String message = restTemplate.getForObject("http://localhost:8080/", String.class);
        assertEquals("hello", message);
    }
}
