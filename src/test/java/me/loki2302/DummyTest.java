package me.loki2302;

import me.loki2302.client.ClientConfiguration;
import me.loki2302.client.NoteClient;
import me.loki2302.server.ServerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.Assert.assertEquals;

public class DummyTest {
    private ConfigurableApplicationContext serverContext;
    private ConfigurableApplicationContext clientContext;

    @Before
    public void start() throws InterruptedException {
        serverContext = SpringApplication.run(ServerConfiguration.class);
        clientContext = new SpringApplicationBuilder(ClientConfiguration.class)
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
    public void canHave2Contexts() {
        NoteClient noteClient = clientContext.getBean(NoteClient.class);

        long noteCount = noteClient.getNoteCount();
        assertEquals(0, noteCount);

        noteClient.createNote("hello there");

        noteCount = noteClient.getNoteCount();
        assertEquals(1, noteCount);
    }
}
