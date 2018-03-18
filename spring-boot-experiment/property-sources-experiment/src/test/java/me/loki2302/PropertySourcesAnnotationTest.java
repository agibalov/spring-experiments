package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertySourcesAnnotationTest {
    @Autowired
    private ConfigurableEnvironment environment;

    @Value("${x}")
    private String x;

    @Value("${exclusive1}")
    private String exclusive1;

    @Value("${exclusive2}")
    private String exclusive2;

    @Value("${y}")
    private String y;

    @Test
    public void canAddPropertySourcesDeclaratively() {
        assertEquals("x from application.properties", x);
        assertEquals("exclusive from 1.properties", exclusive1);
        assertEquals("exclusive from 2.properties", exclusive2);
        assertEquals("y from 2.properties", y);

        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        List<String> propertySourceNames = StreamSupport.stream(mutablePropertySources.spliterator(), false)
                .sorted((ps1, ps2) -> mutablePropertySources.precedenceOf(ps1) - mutablePropertySources.precedenceOf(ps2))
                .map(org.springframework.core.env.PropertySource::getName)
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(
                "configurationProperties", // highest priority
                "Inlined Test Properties",
                "systemProperties",
                "systemEnvironment",
                "random",
                "applicationConfig: [classpath:/application.properties]",
                "class path resource [2.properties]",
                "class path resource [1.properties]" // lowest priority
        ), propertySourceNames);
    }

    @Configuration
    @PropertySources({
            @PropertySource("classpath:1.properties"),
            @PropertySource("classpath:2.properties")
    })
    public static class Config {
    }
}
