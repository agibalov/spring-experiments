package me.loki2302.time;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public abstract class JsonWebSocketHandler<TClientMessage> extends TextWebSocketHandler {
    private final static Logger logger = LoggerFactory.getLogger(JsonWebSocketHandler.class);
    private final ObjectMapper objectMapper;

    public JsonWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String jsonString = message.getPayload();
        TClientMessage clientMessage = null;
        try {
            clientMessage = objectMapper.readValue(jsonString, getClientMessageClass());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        handleJsonMessage(session, clientMessage);
    }

    protected void sendMessage(WebSocketSession session, Object message) {
        try {
            String jsonString = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Class<TClientMessage> getClientMessageClass();
    protected abstract void handleJsonMessage(WebSocketSession webSocketSession, TClientMessage message);
}
