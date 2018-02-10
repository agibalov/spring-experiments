package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("qa")
public class QaConfigTest {
    @Value("${message}")
    public String message;

    @Value("${service.url}")
    public String serviceUrl;

    @Value("${env.url}")
    public String envUrl;

    @Test
    public void test() {
        assertEquals("hi there", message);
        assertEquals("http://qa/my-service", serviceUrl);
        assertEquals("http://qa", envUrl);
    }

    @Configuration
    public static class Config {
    }
}
