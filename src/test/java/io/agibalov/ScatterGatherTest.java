package io.agibalov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.integration.IntegrationMessageHeaderAccessor.CORRELATION_ID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ScatterGatherTest {
    @Autowired
    @Qualifier("input")
    private DirectChannel input;

    @Autowired
    @Qualifier("output")
    private QueueChannel output;

    @Test
    public void scatterGatherShouldScatterGather() {
        input.send(MessageBuilder.withPayload("hello").setHeader(CORRELATION_ID, "xxx").build());
        Message<List<String>> result = (Message<List<String>>) output.receive(0);
        assertEquals("xxx", result.getHeaders().get(CORRELATION_ID));
        List<String> payload = result.getPayload();
        assertEquals(2, payload.size());
        assertTrue(payload.contains("hello - A"));
        assertTrue(payload.contains("hello - B"));
    }

    @Configuration
    @EnableIntegration
    public static class Config {
        @Bean(name = "input")
        public DirectChannel inputChannel() {
            return MessageChannels.direct("input").get();
        }

        @Bean(name = "recepientAInput")
        public DirectChannel recepientAInput() {
            return MessageChannels.direct("recepientAInput").get();
        }

        @Bean(name = "recepientBInput")
        public DirectChannel recepientBInput() {
            return MessageChannels.direct("recepientBInput").get();
        }

        @Bean(name = "output")
        public QueueChannel outputChannel() {
            return MessageChannels.queue("output").get();
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return IntegrationFlows.from(inputChannel())
                    .scatterGather(
                            recipientListRouterSpec -> recipientListRouterSpec
                                    .recipient(recepientAInput())
                                    .recipient(recepientBInput()),
                            aggregatorSpec -> aggregatorSpec.releaseStrategy(group -> group.size() == 2))
                    .channel(outputChannel())
                    .get();
        }

        @Bean
        public IntegrationFlow aIntegrationFlow() {
            return IntegrationFlows.from(recepientAInput())
                    .handle(String.class, (payload, headers) -> String.format("%s - A", payload))
                    .get();
        }

        @Bean
        public IntegrationFlow bIntegrationFlow() {
            return IntegrationFlows.from(recepientBInput())
                    .handle(String.class, (payload, headers) -> String.format("%s - B", payload))
                    .get();
        }
    }
}
