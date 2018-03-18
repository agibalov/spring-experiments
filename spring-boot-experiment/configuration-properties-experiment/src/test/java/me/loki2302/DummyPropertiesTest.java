package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "dummy.message=hi there",
        "dummy.username=qwerty"
})
public class DummyPropertiesTest {
    @Autowired
    private DummyProperties dummyProperties;

    @Test
    public void dummy() {
        assertEquals("hi there", dummyProperties.getMessage());
        assertEquals("qwerty", dummyProperties.getUserName());
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public DummyProperties dummyProperties() {
            return new DummyProperties();
        }
    }

    @ConfigurationProperties("dummy")
    public static class DummyProperties {
        private String message;
        private String userName;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
