package me.loki2302.greetingsapp;

import me.loki2302.greetings.EnableGreetings;
import me.loki2302.greetings.GreetingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnableGreetingsWithDefaultsTest {
    @Autowired
    private GreetingService greetingService;

    @Test
    public void greetingServiceShouldSayDefault() {
        assertEquals("Default, loki2302!", greetingService.greet("loki2302"));
    }

    @Configuration
    @EnableGreetings
    public static class Config {
    }
}
