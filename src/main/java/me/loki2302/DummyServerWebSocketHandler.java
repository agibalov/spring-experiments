package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class DummyServerWebSocketHandler extends TextWebSocketHandler {
    private final static Logger logger = LoggerFactory.getLogger(DummyServerWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("{} connected", session.getRemoteAddress());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("{} says: {}", session.getRemoteAddress(), message.getPayload());
        session.sendMessage(new TextMessage("hello " + message.getPayload() + "!"));
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
