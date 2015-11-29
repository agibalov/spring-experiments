package me.loki2302.greetings;

import org.springframework.beans.factory.annotation.Autowired;

public class GreetingService {
    @Autowired
    private GreetingProvider greetingProvider;

    public String greet(String name) {
        return String.format(greetingProvider.getGreetingTemplate(), name);
    }
}
