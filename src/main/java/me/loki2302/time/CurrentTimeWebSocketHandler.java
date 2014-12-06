package me.loki2302.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CurrentTimeWebSocketHandler extends TextWebSocketHandler {
    private final static Logger logger = LoggerFactory.getLogger(CurrentTimeWebSocketHandler.class);
    private Set<WebSocketSession> sessions = new HashSet<WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("{} connected", session.getRemoteAddress());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("{} says: {}", session.getRemoteAddress(), message.getPayload());
        session.sendMessage(new TextMessage("hello " + message.getPayload() + "!"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("{} disconnected", session.getRemoteAddress());
        sessions.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.info("{} error: {}", session.getRemoteAddress(), exception.getMessage());
    }

    @Scheduled(fixedRate = 1000)
    public void sendCurrentTime() throws IOException {
        for(WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(new Date().toString()));
        }
    }
}
