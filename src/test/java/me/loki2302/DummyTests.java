package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DummyTests {
    @Test
    public void canUseRawWebSockets() throws InterruptedException, ExecutionException, MalformedURLException {
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
    public void canUseSockJs() throws InterruptedException, ExecutionException, MalformedURLException {
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
}
