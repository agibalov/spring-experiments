package me.loki2302.sessions;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class DummyController {
    @RequestMapping(value = "/set-session-attribute", method = RequestMethod.GET)
    public String setSessionAttribute(HttpSession session) {
        session.setAttribute("test", "hello");
        return "hello there";
    }

    @RequestMapping(value = "/get-session-attribute", method = RequestMethod.GET)
    public String getSessionAttribute(HttpSession session) {
        return "hello there: " + session.getAttribute("test");
    }
}
