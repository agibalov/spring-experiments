package me.loki2302.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class GreetingController {
    private final Logger logger = LoggerFactory.getLogger(GreetingController.class);

    @ResponseBody
    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public void authenticate() {

        UsernamePasswordAuthenticationToken t = new UsernamePasswordAuthenticationToken("demouser", "demopass", AuthorityUtils.NO_AUTHORITIES);
        //t.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(t);

    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(HelloMessage helloMessage, Principal principal) {
        logger.info("greeting(): {} (principal={})", helloMessage.name, principal);

        GreetingMessage greetingMessage = new GreetingMessage();
        greetingMessage.message = String.format("Hello, %s! (%s)", helloMessage.name, principal.getName());
        return greetingMessage;
    }
}
