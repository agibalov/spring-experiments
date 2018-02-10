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

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AggregatorTest {
    @Autowired
    @Qualifier("input")
    private DirectChannel input;

    @Autowired
    @Qualifier("output")
    private QueueChannel output;

    @Test
    public void aggregatorShouldAggregate() {
        input.send(MessageBuilder.withPayload("m1").setHeader("myCorrelationKey", "a").build());
        assertNull(output.receive(0));

        input.send(MessageBuilder.withPayload("m2").setHeader("myCorrelationKey", "a").build());
        assertNull(output.receive(0));

        input.send(MessageBuilder.withPayload("m3").setHeader("myCorrelationKey", "a").build());

        Message<String[]> message = (Message<String[]>) output.receive(0);
        assertNotNull(message);

        assertEquals(Arrays.asList("m1", "m2", "m3"), message.getPayload());
        assertEquals("a", message.getHeaders().get("myCorrelationKey"));
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
                    .aggregate(aggregatorSpec -> aggregatorSpec
                            .correlationStrategy(message -> message.getHeaders().get("myCorrelationKey"))
                            .releaseStrategy(messageGroup -> messageGroup.size() > 2))
                    .channel(outputChannel())
                    .get();
        }
    }
}
