package me.loki2302.stomp;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = StompConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StompTests {
    private static final Logger logger = LoggerFactory.getLogger(StompTests.class);

    @Test
    public void canUseStomp() throws InterruptedException, ExecutionException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        Exchanger<String> receivedMessageExchanger = new Exchanger<>();
        GreetingsFrameHandler greetingsFrameHandler = new GreetingsFrameHandler(receivedMessageExchanger);
        AppStompSessionHandler stompSessionHandler = new AppStompSessionHandler(greetingsFrameHandler, SubscriptionTarget.PUBLIC);

        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization", makeBasicHttpAuthorizationToken("testuser", "testpassword"));

        StompSession stompSession = webSocketStompClient.connect(
                "ws://localhost:8080/ws",
                webSocketHttpHeaders,
                stompSessionHandler).get();

        String receivedMessage = receivedMessageExchanger.exchange(null);
        assertEquals("Hello, qwerty! (testuser)", receivedMessage);

        stompSession.disconnect();
    }

    @Test
    public void canUseStompWithUserScopedSubscription() throws InterruptedException, ExecutionException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        Exchanger<String> receivedMessageExchanger = new Exchanger<>();
        GreetingsFrameHandler greetingsFrameHandler = new GreetingsFrameHandler(receivedMessageExchanger);
        AppStompSessionHandler stompSessionHandler = new AppStompSessionHandler(greetingsFrameHandler, SubscriptionTarget.PERSONAL);

        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization", makeBasicHttpAuthorizationToken("testuser", "testpassword"));

        StompSession stompSession = webSocketStompClient.connect(
                "ws://localhost:8080/ws",
                webSocketHttpHeaders,
                stompSessionHandler).get();

        String receivedMessage = receivedMessageExchanger.exchange(null);
        assertEquals("Hello, qwerty! (testuser)", receivedMessage);

        stompSession.disconnect();
    }

    private static String makeBasicHttpAuthorizationToken(String username, String password) {
        String usernameColonPasswordString = String.format("%s:%s", username, password);
        String base64EncodedUsernameColonPasswordString = Base64.encodeBase64String(usernameColonPasswordString.getBytes());
        String tokenString = String.format("Basic %s", base64EncodedUsernameColonPasswordString);
        return tokenString;
    }

    public static class GreetingsFrameHandler implements StompFrameHandler {
        private static final Logger logger = LoggerFactory.getLogger(GreetingsFrameHandler.class);
        private final Exchanger<String> receivedMessageExchanger;

        public GreetingsFrameHandler(Exchanger<String> receivedMessageExchanger) {
            this.receivedMessageExchanger = receivedMessageExchanger;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GreetingMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            GreetingMessage greetingMessage = (GreetingMessage)payload;
            logger.info("Got GreetingMessage: {}", greetingMessage.message);

            try {
                receivedMessageExchanger.exchange(greetingMessage.message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public enum SubscriptionTarget {
        PUBLIC,
        PERSONAL
    }

    public static class AppStompSessionHandler implements StompSessionHandler {
        private static final Logger logger = LoggerFactory.getLogger(AppStompSessionHandler.class);
        private final GreetingsFrameHandler greetingsFrameHandler;
        private final SubscriptionTarget subscriptionTarget;

        public AppStompSessionHandler(GreetingsFrameHandler greetingsFrameHandler, SubscriptionTarget subscriptionTarget) {
            this.greetingsFrameHandler = greetingsFrameHandler;
            this.subscriptionTarget = subscriptionTarget;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            logger.info("afterConnected()", session);

            if(subscriptionTarget.equals(SubscriptionTarget.PUBLIC)) {
                // subscribe to public notifications (i.e. sent to all users)
                session.subscribe("/out/greetings", greetingsFrameHandler);
            } else if(subscriptionTarget.equals(SubscriptionTarget.PERSONAL)) {
                // subscribe to private notifications (i.e. sent to this specific user)
                session.subscribe("/user/testuser/out/greetings", greetingsFrameHandler);
            } else {
                throw new RuntimeException();
            }

            HelloMessage helloMessage = new HelloMessage();
            helloMessage.name = "qwerty";

            if(subscriptionTarget.equals(SubscriptionTarget.PUBLIC)) {
                session.send("/in/hello", helloMessage);
            } else if(subscriptionTarget.equals(SubscriptionTarget.PERSONAL)) {
                session.send("/in/personal-hello", helloMessage);
            } else {
                throw new RuntimeException();
            }
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            logger.info("handleException()", exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            logger.info("handleTransportError()", exception);
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            logger.info("getPayloadType()");
            return null;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            logger.info("handleMessage()");
        }
    }
}
