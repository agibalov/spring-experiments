package me.loki2302;

import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CustomPropertySourcesWithApplicationContextInitializerTest {
    @Test
    public void canOverrideBootsPropertySourcesConfigurationWithApplicationContextInitializer() {
        try(ConfigurableApplicationContext context = new SpringApplicationBuilder(Config.class)
                .web(false)
                .initializers(new MyApplicationContextInitializer())
                .run()) {

            ConfigurableEnvironment environment = context.getBean(ConfigurableEnvironment.class);
            assertEquals("x from 2.properties", environment.getProperty("x"));
            assertEquals("exclusive from 2.properties", environment.getProperty("exclusive2"));
            assertEquals("y from 2.properties", environment.getProperty("y"));
            assertNull(environment.getProperty("exclusive1"));

            MutablePropertySources mutablePropertySources = environment.getPropertySources();
            List<String> propertySourceNames = StreamSupport.stream(mutablePropertySources.spliterator(), false)
                    .map(PropertySource::getName)
                    .collect(Collectors.toList());
            assertEquals(1, propertySourceNames.size());
            assertEquals("class path resource [2.properties]", propertySourceNames.get(0));
        }
    }

    public static class MyApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            ConfigurableEnvironment environment = context.getEnvironment();
            MutablePropertySources mutablePropertySources = environment.getPropertySources();

            List<String> propertySourceNames = StreamSupport.stream(mutablePropertySources.spliterator(), false)
                    .map(PropertySource::getName)
                    .collect(Collectors.toList());
            propertySourceNames.forEach(mutablePropertySources::remove);

            PropertySource<?> twoPropertiesPropertySource;
            try {
                twoPropertiesPropertySource = new ResourcePropertySource("classpath:2.properties");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mutablePropertySources.addFirst(twoPropertiesPropertySource);
        }
    }

    @Configuration
    public static class Config {
    }
}
