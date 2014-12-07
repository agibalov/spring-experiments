package me.loki2302.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {
    private final Logger logger = LoggerFactory.getLogger(GreetingController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(HelloMessage helloMessage) {
        logger.info("Someone connected: {}", helloMessage.name);

        GreetingMessage greetingMessage = new GreetingMessage();
        greetingMessage.message = String.format("Hello, %s!", helloMessage.name);
        return greetingMessage;
    }
}
