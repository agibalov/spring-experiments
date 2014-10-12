//
// Gratefully copypasted and adapted from spring-websocket-portfolio by Rossen Stoyanchev:
// https://github.com/rstoyanchev/spring-websocket-portfolio/tree/master/src/test/java/org/springframework/samples/portfolio/web/support/client
//

package me.loki2302.stomp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class WebSocketStompClient {
    private static Log logger = LogFactory.getLog(WebSocketStompClient.class);
    private final URI uri;
    private final WebSocketHttpHeaders headers;
    private final WebSocketClient webSocketClient;
    private MessageConverter messageConverter;

    public WebSocketStompClient(URI uri, WebSocketHttpHeaders headers, WebSocketClient webSocketClient) {
        this.uri = uri;
        this.headers = headers;
        this.webSocketClient = webSocketClient;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void connect(StompMessageHandler stompMessageHandler) {
        try {
            StompWebSocketHandler webSocketHandler = new StompWebSocketHandler(
                    stompMessageHandler,
                    messageConverter);
            webSocketClient.doHandshake(webSocketHandler, headers, uri).get();
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static class StompWebSocketHandler extends AbstractWebSocketHandler {
        private static final Charset UTF_8 = Charset.forName("UTF-8");
        private final StompMessageHandler stompMessageHandler;
        private final MessageConverter messageConverter;
        private final StompEncoder encoder = new StompEncoder();
        private final StompDecoder decoder = new StompDecoder();

        private StompWebSocketHandler(StompMessageHandler delegate, MessageConverter messageConverter) {
            this.stompMessageHandler = delegate;
            this.messageConverter = messageConverter;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);
            headers.setAcceptVersion("1.1,1.2");
            headers.setHeartbeat(0, 0);
            Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

            TextMessage textMessage = new TextMessage(new String(this.encoder.encode(message), UTF_8));
            session.sendMessage(textMessage);
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
            ByteBuffer payload = ByteBuffer.wrap(textMessage.getPayload().getBytes(UTF_8));
            List<Message<byte[]>> messages = this.decoder.decode(payload);

            for (Message message : messages) {
                StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
                StompCommand stompCommand = headers.getCommand();
                if (StompCommand.CONNECTED.equals(stompCommand)) {
                    logger.info("Connected");

                    WebSocketStompSession stompSession = new WebSocketStompSession(session, messageConverter);
                    stompMessageHandler.afterConnected(stompSession, headers);
                } else if (StompCommand.MESSAGE.equals(stompCommand)) {
                    logger.info("Message");
                    stompMessageHandler.handleMessage(message);
                } else if (StompCommand.RECEIPT.equals(stompCommand)) {
                    logger.info("Receipt");
                    stompMessageHandler.handleReceipt(headers.getReceiptId());
                } else if (StompCommand.ERROR.equals(stompCommand)) {
                    logger.info("Error");
                    stompMessageHandler.handleError(message);
                } else if (StompCommand.DISCONNECT.equals(stompCommand)) {
                    logger.info("Disconnect");
                    stompMessageHandler.afterDisconnected();
                } else {
                    logger.info("Unhandled message " + message);
                }
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            logger.error("WebSocket transport error", exception);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            this.stompMessageHandler.afterDisconnected();
        }
    }
}