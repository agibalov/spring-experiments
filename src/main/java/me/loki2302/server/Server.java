package me.loki2302.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
public class Server {
    @RequestMapping("/")
    public String hello() {
        return "hello";
    }
}
