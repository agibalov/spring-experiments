package me.loki2302.stomp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = StompConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StompTests {
    @Test
    public void canUseStomp() throws InterruptedException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        CountDownLatch countDownLatch = new CountDownLatch(1);
        webSocketStompClient.connect("ws://localhost:8080/hello", new CountDownLatchStompSessionHandler(countDownLatch));
        countDownLatch.await();
    }

    public static class CountDownLatchStompSessionHandler implements StompSessionHandler {
        private static final Logger logger = LoggerFactory.getLogger(CountDownLatchStompSessionHandler.class);
        private final CountDownLatch countDownLatch;

        public CountDownLatchStompSessionHandler(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            logger.info("afterConnected()", session);

            // TODO: should I use a different handle object instead of this?
            session.subscribe("/topic/greetings", this);

            HelloMessage helloMessage = new HelloMessage();
            helloMessage.name = "qwerty";
            session.send("/time/hello", helloMessage);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            logger.info("handleException() {}", exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            logger.info("handleTransportError()");
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GreetingMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            logger.info("handleMessage()");

            GreetingMessage greetingMessage = (GreetingMessage)payload;
            logger.info("message is: {}", greetingMessage.message);

            countDownLatch.countDown();
        }
    }
}
