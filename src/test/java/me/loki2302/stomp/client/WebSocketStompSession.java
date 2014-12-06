//
// Gratefully copypasted and adapted from spring-websocket-portfolio by Rossen Stoyanchev:
// https://github.com/rstoyanchev/spring-websocket-portfolio/tree/master/src/test/java/org/springframework/samples/portfolio/web/support/client
//

package me.loki2302.stomp.client;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketStompSession {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final byte[] EMPTY_PAYLOAD = new byte[0];
    private final String id;
    private final WebSocketSession webSocketSession;
    private final MessageConverter messageConverter;
    private final StompEncoder encoder = new StompEncoder();
    private final AtomicInteger subscriptionIndex = new AtomicInteger();

    public WebSocketStompSession(WebSocketSession webSocketSession, MessageConverter messageConverter) {
        Assert.notNull(webSocketSession);
        Assert.notNull(messageConverter);
        this.id = webSocketSession.getId();
        this.webSocketSession = webSocketSession;
        this.messageConverter = messageConverter;
    }

    public void subscribe(String destination, String receiptId) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("sub" + subscriptionIndex.getAndIncrement());
        headers.setDestination(destination);
        if (receiptId != null) {
            headers.setReceipt(receiptId);
        }
        sendInternal(MessageBuilder.withPayload(EMPTY_PAYLOAD).setHeaders(headers).build());
    }

    public void send(String destination, Object payload) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination(destination);
        sendInternal((Message<byte[]>)messageConverter.toMessage(payload, new MessageHeaders(headers.toMap())));
    }

    public void disconnect() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        Message<byte[]> message = MessageBuilder.withPayload(EMPTY_PAYLOAD).setHeaders(headers).build();
        sendInternal(message);
        try {
            webSocketSession.close();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void sendInternal(Message<byte[]> message) {
        byte[] bytes = this.encoder.encode(message);
        try {
            webSocketSession.sendMessage(new TextMessage(new String(bytes, UTF_8)));
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return webSocketSession.toString();
    }
}