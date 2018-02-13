package io.agibalov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ErrorHandlingTest {
    @Autowired
    @Qualifier("input")
    private QueueChannel input;

    @Autowired
    @Qualifier("output")
    private QueueChannel output;

    @Autowired
    @Qualifier("error")
    private QueueChannel error;

    @Test
    public void errorsShouldGetReportedToErrorChannel() {
        input.send(MessageBuilder.withPayload("hello")
                .setHeader(MessageHeaders.ERROR_CHANNEL, "error")
                .build());

        Message<?> errorMessage = error.receive(1000);
        MessageTransformationException exception = (MessageTransformationException) errorMessage.getPayload();
        assertEquals("hello", exception.getFailedMessage().getPayload());
        assertEquals("some random processing error", exception.getCause().getCause().getMessage());
    }

    @Configuration
    @EnableIntegration
    public static class Config {
        @Bean(name = "input")
        public QueueChannel inputChannel() {
            return MessageChannels.queue("input").get();
        }

        @Bean(name = "output")
        public QueueChannel outputChannel() {
            return MessageChannels.queue("output").get();
        }

        @Bean(name = "error")
        public QueueChannel errorChannel() {
            return MessageChannels.queue("error").get();
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return IntegrationFlows.from(inputChannel())
                    .transform(String.class, s -> {
                        throw new RuntimeException("some random processing error");
                    }, s -> s.poller(Pollers.fixedRate(100)))
                    .channel(outputChannel())
                    .get();
        }
    }
}
