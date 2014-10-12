//
// Gratefully copypasted and adapted from spring-websocket-portfolio by Rossen Stoyanchev:
// https://github.com/rstoyanchev/spring-websocket-portfolio/tree/master/src/test/java/org/springframework/samples/portfolio/web/support/client
//

package me.loki2302.stomp;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompMessageHandler {
    void afterConnected(WebSocketStompSession session, StompHeaderAccessor headers);
    void handleMessage(Message<byte[]> message);
    void handleReceipt(String receiptId);
    void handleError(Message<byte[]> message);
    void afterDisconnected();
}