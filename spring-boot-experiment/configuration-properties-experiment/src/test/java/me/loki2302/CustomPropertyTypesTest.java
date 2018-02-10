package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "dummy.someDuration=PT15M"
})
public class CustomPropertyTypesTest {
    @Autowired
    private DummyProperties dummyProperties;

    @Test
    public void canConvertStringToDuration() {
        assertEquals(15, dummyProperties.getSomeDuration().toMinutes());
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public DummyProperties dummyProperties() {
            return new DummyProperties();
        }

        @Bean
        public DurationConverter durationConverter() {
            return new DurationConverter();
        }
    }

    @ConfigurationPropertiesBinding
    public static class DurationConverter implements Converter<String, Duration> {
        @Override
        public Duration convert(String source) {
            if(source == null) {
                return null;
            }
            return Duration.parse(source);
        }
    }

    @ConfigurationProperties("dummy")
    public static class DummyProperties {
        private Duration someDuration;

        public Duration getSomeDuration() {
            return someDuration;
        }

        public void setSomeDuration(Duration someDuration) {
            this.someDuration = someDuration;
        }
    }
}
