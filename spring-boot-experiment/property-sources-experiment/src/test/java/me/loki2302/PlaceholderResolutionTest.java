package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "something=hello world")
public class PlaceholderResolutionTest {
    @Autowired
    private ConfigurableEnvironment environment;

    @Test
    public void canResolvePlaceholders() {
        assertEquals("hello world", environment.resolvePlaceholders("${something}"));
        assertEquals("PREFIXhello worldSUFFIX", environment.resolvePlaceholders("PREFIX${something}SUFFIX"));
        assertEquals("some default", environment.resolvePlaceholders("${somethingElse:some default}"));
        assertEquals("hi there", environment.resolvePlaceholders("hi there"));
    }

    @Configuration
    public static class Config {
    }
}
