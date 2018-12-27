package me.loki2302.servlet;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SseEmitterTest {
    @Autowired
    private Exchanger<Object> sendExchanger;

    @Test
    public void itShouldWork() throws InterruptedException {
        Exchanger<Object> receiveExchanger = new Exchanger<>();
        ExchangingEventHandler exchangingEventHandler = new ExchangingEventHandler(receiveExchanger);
        try(EventSource eventSource = new EventSource.Builder(
                exchangingEventHandler,
                URI.create("http://localhost:8080/")).build()) {

            eventSource.start();

            for(int i = 0; i < 3; ++i) {
                sendExchanger.exchange(null);
                assertEquals(String.format("hi there #%d", i), receiveExchanger.exchange(null));
            }
        }
    }

    public static class ExchangingEventHandler implements EventHandler {
        private final static Logger logger = LoggerFactory.getLogger(ExchangingEventHandler.class);

        private final Exchanger<Object> exchanger;

        public ExchangingEventHandler(Exchanger<Object> exchanger) {
            this.exchanger = exchanger;
        }

        @Override
        public void onOpen() {
            logger.info("onOpen");
        }

        @Override
        public void onClosed() {
            logger.info("onClosed");
        }

        @Override
        public void onMessage(String event, MessageEvent messageEvent) {
            logger.info("onMessage: event={} data={}", event, messageEvent.getData());
            try {
                this.exchanger.exchange(messageEvent.getData());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onComment(String comment) {
            logger.info("onComment: {}", comment);
        }

        @Override
        public void onError(Throwable t) {
            logger.info("onError: {}", t);
        }
    }

    @RestController
    public static class DummyController {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        @Autowired
        private Exchanger<Object> exchanger;

        @GetMapping
        public ResponseEntity<SseEmitter> sseEmitterHandler() {
            SseEmitter sseEmitter = new SseEmitter();

            executorService.submit(() -> {
                try {
                    for(int i = 0; i < 3; ++i) {
                        exchanger.exchange(null);
                        sseEmitter.send(String.format("hi there #%d\n", i));
                    }
                    sseEmitter.complete();
                } catch (Throwable e) {
                    sseEmitter.completeWithError(e);
                }
            });

            return ResponseEntity.ok(sseEmitter);
        }
    }

    @SpringBootApplication
    public static class Config {
        @Bean
        public Exchanger<Object> exchanger() {
            return new Exchanger<>();
        }

        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }
}
