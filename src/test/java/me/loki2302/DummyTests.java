package me.loki2302;

import me.loki2302.stomp.StompMessageHandler;
import me.loki2302.stomp.WebSocketStompClient;
import me.loki2302.stomp.WebSocketStompSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = AppConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DummyTests {
    @Test
    public void canUseRawWebSockets() throws InterruptedException {
        Exchanger<String> messageExchanger = new Exchanger<String>();

        WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                new DummyClientWebSocketHandler(messageExchanger),
                "ws://localhost:8080/raw");
        webSocketConnectionManager.start();

        String message = messageExchanger.exchange(null);
        assertEquals("hello loki2302!", message);
    }

    @Test
    public void canUseSockJs() throws InterruptedException {
        Exchanger<String> messageExchanger = new Exchanger<String>();

        SockJsClient sockJsClient = new SockJsClient(Arrays.<Transport>asList(
                new RestTemplateXhrTransport(
                        new RestTemplate(new HttpComponentsClientHttpRequestFactory()))));
        WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
                sockJsClient,
                new DummyClientWebSocketHandler(messageExchanger),
                "ws://localhost:8080/sockjs");
        webSocketConnectionManager.start();

        String message = messageExchanger.exchange(null);
        assertEquals("hello loki2302!", message);
    }

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

    public static class DummyClientWebSocketHandler extends TextWebSocketHandler {
        private final static Logger logger = LoggerFactory.getLogger(DummyClientWebSocketHandler.class);

        private final Exchanger<String> messageExchanger;

        public DummyClientWebSocketHandler(Exchanger<String> messageExchanger) {
            this.messageExchanger = messageExchanger;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            logger.info("connected to {}", session.getRemoteAddress());
            session.sendMessage(new TextMessage("loki2302"));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            logger.info("{} says: {}", session.getRemoteAddress(), message.getPayload());
            messageExchanger.exchange(message.getPayload());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            logger.info("{} disconnected", session.getRemoteAddress());
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            logger.info("{} error: {}", session.getRemoteAddress(), exception.getMessage());
        }
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
            session.send("/app/hello", helloMessage);
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
