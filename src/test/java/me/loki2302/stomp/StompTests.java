package me.loki2302.stomp;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = StompConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StompTests {
    private static final Logger logger = LoggerFactory.getLogger(StompTests.class);

    @Test
    public void canUseStomp() throws InterruptedException, ExecutionException {
        // This request is only needed to get a JSESSIONID cookie
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ResponseEntity responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/auth", Object.class);
        HttpHeaders headers = responseEntity.getHeaders();
        logger.info("Backend sent me these cookies: {}", headers);
        assertTrue(headers.containsKey("Set-Cookie"));

        // Travis won't let me use the real WebSockets, so I'm using the SockJS client
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        Exchanger<String> receivedMessageExchanger = new Exchanger<String>();
        GreetingsFrameHandler greetingsFrameHandler = new GreetingsFrameHandler(receivedMessageExchanger);
        AppStompSessionHandler stompSessionHandler = new AppStompSessionHandler(greetingsFrameHandler);

        String jSessionIdCookieValue = cookieStore.getCookies().stream()
                .filter(c -> c.getName().equals("JSESSIONID"))
                .findFirst().get().getValue();

        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Cookie", "JSESSIONID=" + jSessionIdCookieValue);
        logger.info("Got these cookies: {}", webSocketHttpHeaders);

        StompSession stompSession = webSocketStompClient.connect(
                "ws://localhost:8080/hello",
                webSocketHttpHeaders,
                stompSessionHandler).get();

        String receivedMessage = receivedMessageExchanger.exchange(null);
        assertEquals("Hello, qwerty! (demouser)", receivedMessage);

        stompSession.disconnect();
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

    public static class AppStompSessionHandler implements StompSessionHandler {
        private static final Logger logger = LoggerFactory.getLogger(AppStompSessionHandler.class);
        private final GreetingsFrameHandler greetingsFrameHandler;

        public AppStompSessionHandler(GreetingsFrameHandler greetingsFrameHandler) {
            this.greetingsFrameHandler = greetingsFrameHandler;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            logger.info("afterConnected()", session);

            session.subscribe("/topic/greetings", greetingsFrameHandler);

            HelloMessage helloMessage = new HelloMessage();
            helloMessage.name = "qwerty";
            session.send("/app/hello", helloMessage);
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
