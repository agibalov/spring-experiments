package me.loki2302.stomp;

import me.loki2302.stomp.client.StompMessageHandler;
import me.loki2302.stomp.client.WebSocketStompClient;
import me.loki2302.stomp.client.WebSocketStompSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = StompConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StompTests {
    @Test
    public void canUseStomp() throws InterruptedException {
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(
                URI.create("ws://localhost:8080/hello"),
                null,
                new StandardWebSocketClient());
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        CountDownLatch countDownLatch = new CountDownLatch(1);
        webSocketStompClient.connect(new CountDownLatchStompMessageHandler(countDownLatch), GreetingMessage.class);
        countDownLatch.await();
    }

    public static class CountDownLatchStompMessageHandler implements StompMessageHandler<GreetingMessage> {
        private final Logger logger;
        private final CountDownLatch countDownLatch;

        public CountDownLatchStompMessageHandler(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
            logger = LoggerFactory.getLogger(StompMessageHandler.class);
        }

        @Override
        public void afterConnected(WebSocketStompSession session, StompHeaderAccessor headers) {
            logger.info("afterConnected()", session);

            session.subscribe("/topic/greetings", null);

            HelloMessage helloMessage = new HelloMessage();
            helloMessage.name = "qwerty";
            session.send("/time/hello", helloMessage);
        }

        @Override
        public void handleMessage(Message<GreetingMessage> message) {
            logger.info("handleMessage()");

            GreetingMessage greetingMessage = message.getPayload();
            logger.info("message is: {}", greetingMessage.message);

            countDownLatch.countDown();
        }

        @Override
        public void handleReceipt(String receiptId) {
            logger.info("handleReceipt()");
        }

        @Override
        public void handleError(Message<byte[]> message) {
            logger.info("handleError()");
        }

        @Override
        public void afterDisconnected() {
            logger.info("afterDisconnected()");
        }
    }
}
