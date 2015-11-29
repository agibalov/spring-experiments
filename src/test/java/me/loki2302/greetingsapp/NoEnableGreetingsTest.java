package me.loki2302.greetingsapp;

import me.loki2302.greetings.GreetingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class NoEnableGreetingsTest {
    @Autowired
    private ApplicationContext context;

    @Test
    public void greetingServiceBeanShouldNotExist() {
        try {
            context.getBean(GreetingService.class);
            fail();
        } catch(NoSuchBeanDefinitionException e) {
            assertEquals(GreetingService.class, e.getBeanType());
        }
    }

    @Configuration
    public static class Config {
    }
}
