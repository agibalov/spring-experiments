package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventsTest {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private MyEventListener myEventListener;

    @Test
    public void dummy() {
        applicationEventPublisher.publishEvent(new MyEvent());
        assertEquals(1, myEventListener.eventCount);

        applicationEventPublisher.publishEvent(new MyEvent());
        assertEquals(2, myEventListener.eventCount);
    }

    @Configuration
    public static class Config {
        @Bean
        public MyEventListener myEventListener() {
            return new MyEventListener();
        }
    }

    public static class MyEvent {}

    public static class MyEventListener {
        public int eventCount;

        @EventListener
        public void handle(MyEvent myEvent) {
            ++eventCount;
        }
    }
}
