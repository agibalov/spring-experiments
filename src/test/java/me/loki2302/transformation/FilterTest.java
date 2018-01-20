package me.loki2302.transformation;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FilterTest {
    @Autowired
    @Qualifier("input")
    private DirectChannel input;

    @Autowired
    @Qualifier("output")
    private QueueChannel output;

    @Autowired
    @Qualifier("discard")
    private QueueChannel discard;

    @Test
    public void filterShouldFilter() {
        Message<String> helloMessage = MessageBuilder.withPayload("hello").build();
        input.send(helloMessage);
        assertEquals(helloMessage, output.receive(0));
        assertNull(discard.receive(0));

        Message<String> byeMessage = MessageBuilder.withPayload("bye").build();
        input.send(byeMessage);
        assertNull(output.receive(0));
        assertEquals(byeMessage, discard.receive(0));
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

        @Bean(name = "discard")
        public QueueChannel discardChannel() {
            return MessageChannels.queue("discard").get();
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return IntegrationFlows.from(inputChannel())
                    .filter(String.class, s -> s.equals("hello"), f -> f.discardChannel(discardChannel()))
                    .channel(outputChannel())
                    .get();
        }
    }
}
