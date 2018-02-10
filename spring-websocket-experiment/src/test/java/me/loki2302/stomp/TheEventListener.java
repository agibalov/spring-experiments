package me.loki2302.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class TheEventListener {
    private final Logger logger = LoggerFactory.getLogger(TheEventListener.class);

    @EventListener
    public void handleSessionConnected(SessionConnectEvent e) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(e.getMessage());
        logger.info("handleSessionConnected(): sessionId={}, destination={}", headerAccessor.getSessionId(), headerAccessor.getDestination());
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent e) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(e.getMessage());
        logger.info("handleSessionDisconnected(): sessionId={}, destination={}", headerAccessor.getSessionId(), headerAccessor.getDestination());
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent e) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(e.getMessage());
        logger.info("handleSessionSubscribeEvent(): sessionId={}, destination={}", headerAccessor.getSessionId(), headerAccessor.getDestination());
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent e) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(e.getMessage());
        logger.info("handleSessionUnsubscribeEvent(): sessionId={}, destination={}", headerAccessor.getSessionId(), headerAccessor.getDestination());
    }
}
