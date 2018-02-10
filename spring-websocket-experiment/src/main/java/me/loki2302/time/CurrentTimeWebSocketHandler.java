package me.loki2302.time;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.loki2302.time.messages.ClientMessage;
import me.loki2302.time.messages.CurrentTimeUpdateMessage;
import me.loki2302.time.messages.NewChatMessage;
import me.loki2302.time.messages.PostChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CurrentTimeWebSocketHandler extends JsonWebSocketHandler<ClientMessage> {
    private final static Logger logger = LoggerFactory.getLogger(CurrentTimeWebSocketHandler.class);
    private Set<WebSocketSession> sessions = new HashSet<WebSocketSession>();

    public CurrentTimeWebSocketHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("{} connected", session.getRemoteAddress());
        sessions.add(session);
    }

    @Override
    protected void handleJsonMessage(WebSocketSession session, ClientMessage message) {
        logger.info("{} says: {}", session.getRemoteAddress(), message.toString());
        if(message instanceof PostChatMessage) {
            NewChatMessage newChatMessage = new NewChatMessage();
            newChatMessage.text = ((PostChatMessage)message).text;

            for(WebSocketSession s : sessions) {
                sendMessage(s, newChatMessage);
            }
        } else {
            logger.warn("Don't know how to handle message of type " + message.getClass());
        }
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
        CurrentTimeUpdateMessage message = new CurrentTimeUpdateMessage();
        message.time = new Date().toString();

        for(WebSocketSession session : sessions) {
            sendMessage(session, message);
        }
    }

    @Override
    protected Class<ClientMessage> getClientMessageClass() {
        return ClientMessage.class;
    }
}
