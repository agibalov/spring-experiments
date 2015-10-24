package me.loki2302.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GreetingController {
    private final Logger logger = LoggerFactory.getLogger(GreetingController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(HelloMessage helloMessage, Principal principal) {
        logger.info("greeting(): {} (principal={})", helloMessage.name, principal);

        GreetingMessage greetingMessage = new GreetingMessage();
        greetingMessage.message = String.format("Hello, %s! (%s)", helloMessage.name, principal.getName());
        return greetingMessage;
    }
}
