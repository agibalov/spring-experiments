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
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.springframework.integration.IntegrationMessageHeaderAccessor.CORRELATION_ID;
import static org.springframework.integration.IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ResequencerTest {
    @Autowired
    @Qualifier("input")
    private DirectChannel input;

    @Autowired
    @Qualifier("output")
    private QueueChannel output;

    @Test
    public void resequencerShouldResequence() {
        input.send(MessageBuilder.withPayload("m1")
                .setHeader(CORRELATION_ID, "a")
                .setHeader(SEQUENCE_NUMBER, 3)
                .build());

        input.send(MessageBuilder.withPayload("m2")
                .setHeader(CORRELATION_ID, "a")
                .setHeader(SEQUENCE_NUMBER, 2)
                .build());

        input.send(MessageBuilder.withPayload("m3")
                .setHeader(CORRELATION_ID, "a")
                .setHeader(SEQUENCE_NUMBER, 1)
                .build());

        assertEquals("m3", output.receive(0).getPayload());
        assertEquals("m2", output.receive(0).getPayload());
        assertEquals("m1", output.receive(0).getPayload());
    }

    @Configuration
    @EnableIntegration
    public static class Config {
        @Bean(name = "input")
        public DirectChannel inputChannel() {
            return MessageChannels.direct("input").get();
        }

        @Bean(name = "output")
        public QueueChannel outputChannel() {
            return MessageChannels.queue("output").get();
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return IntegrationFlows.from(inputChannel())
                    .resequence(resequencerSpec -> resequencerSpec
                            .releaseStrategy(messageGroup -> messageGroup.size() == 3))
                    .channel(outputChannel())
                    .get();
        }
    }
}
