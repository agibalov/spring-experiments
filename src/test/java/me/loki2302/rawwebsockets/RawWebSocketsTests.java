package me.loki2302.rawwebsockets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.Exchanger;

import static org.junit.Assert.assertEquals;

// This test passes, but when it comes to shutdown, Spring says:
// Unable to destroy WebSocket thread group [WebSocketServer-localhost-ROOT] as [1] threads
// were still running when the web application was stopped. The thread group will be destroyed
// once the threads terminate.
// TODO: fix it
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = RawWebSocketConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RawWebSocketsTests {
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
