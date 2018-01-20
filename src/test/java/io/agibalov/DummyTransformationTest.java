package io.agibalov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DummyTransformationTest {
    @Autowired
    private ExclamationMarkService exclamationMarkService;

    @Test
    public void canAddExclamationMark() {
        assertEquals("hello!", exclamationMarkService.addExclamationMark("hello"));
    }

    @Configuration
    @EnableIntegration
    @IntegrationComponentScan(basePackageClasses = DummyTransformationTest.class)
    public static class Config {
        @Bean(name = "out")
        public MessageChannel inChannel() {
            return new DirectChannel();
        }

        @Bean(name = "in")
        public MessageChannel outChannel() {
            return new DirectChannel();
        }

        @Transformer(inputChannel = "in", outputChannel = "out")
        public String transform(String x) {
            return x + "!";
        }
    }

    @MessagingGateway
    public static interface ExclamationMarkService {
        @Gateway(requestChannel = "in", replyChannel = "out")
        String addExclamationMark(String s);
    }
}
