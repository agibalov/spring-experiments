package me.loki2302.sockjs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

// TODO: it looks like when using RestTemplateXhrTransport, session.close() doesn't work, try to fix
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = SockJsConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SockJsTests {
    private final static Logger logger = LoggerFactory.getLogger(SockJsTests.class);

    @Test
    public void canUseSockJs() throws InterruptedException {
        Exchanger<String> messageExchanger = new Exchanger<String>();
        CountDownLatch disconnectCountDownLatch = new CountDownLatch(1);

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        Transport restTemplateXhrTransport = new RestTemplateXhrTransport(restTemplate);
        SockJsClient sockJsClient = new SockJsClient(Collections.singletonList(restTemplateXhrTransport));
        DummyClientWebSocketHandler webSocketHandler = new DummyClientWebSocketHandler(
                messageExchanger,
                disconnectCountDownLatch);
        WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
                sockJsClient,
                webSocketHandler,
                "ws://localhost:8080/sockjs");
        webSocketConnectionManager.start();

        logger.info("receiving message");
        String message = messageExchanger.exchange(null);
        assertEquals("hello loki2302!", message);
        logger.info("received message");

        logger.info("awaiting disconnection");
        if(disconnectCountDownLatch.await(3, TimeUnit.SECONDS)) {
            logger.info("disconnected");
        } else {
            logger.warn("failed to disconnect gracefully");
        }
    }

    public static class DummyClientWebSocketHandler extends TextWebSocketHandler {
        private final static Logger logger = LoggerFactory.getLogger(DummyClientWebSocketHandler.class);

        private final Exchanger<String> messageExchanger;
        private final CountDownLatch disconnectCountDownLatch;

        public DummyClientWebSocketHandler(
                Exchanger<String> messageExchanger,
                CountDownLatch disconnectCountDownLatch) {

            this.messageExchanger = messageExchanger;
            this.disconnectCountDownLatch = disconnectCountDownLatch;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            logger.info("connected to {}", session.getRemoteAddress());
            session.sendMessage(new TextMessage("loki2302"));
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            logger.info("{} disconnected", session.getRemoteAddress());
            logger.info("updating countdownlatch");
            disconnectCountDownLatch.countDown();
            logger.info("updated countdownlatch");
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            logger.info("{} says: {}", session.getRemoteAddress(), message.getPayload());
            logger.info("exchanging message");
            messageExchanger.exchange(message.getPayload());
            logger.info("exchanged message");
            logger.info("closing session");
            session.close();
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            logger.info("{} error: {}", session.getRemoteAddress(), exception.getMessage());
        }
    }
}
