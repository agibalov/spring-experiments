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

@SpringBootTest
@RunWith(SpringRunner.class)
public class RouterTest {
    @Autowired
    @Qualifier("input")
    private DirectChannel input;

    @Autowired
    @Qualifier("outputA")
    private QueueChannel outputA;

    @Autowired
    @Qualifier("outputB")
    private QueueChannel outputB;

    @Test
    public void routerShouldRoute() {
        input.send(MessageBuilder.withPayload("hi 1").build());
        input.send(MessageBuilder.withPayload("hi 2").build());
        input.send(MessageBuilder.withPayload("bye 1").build());

        assertEquals("hi 1", outputA.receive(0).getPayload());
        assertEquals("hi 2", outputA.receive(0).getPayload());
        assertEquals("bye 1", outputB.receive(0).getPayload());
    }

    @Configuration
    @EnableIntegration
    public static class Config {
        @Bean(name = "input")
        public DirectChannel inputChannel() {
            return MessageChannels.direct("input").get();
        }

        @Bean(name = "outputA")
        public QueueChannel outputAChannel() {
            return MessageChannels.queue("outputA").get();
        }

        @Bean(name = "outputB")
        public QueueChannel outputBChannel() {
            return MessageChannels.queue("outputB").get();
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return IntegrationFlows.from(inputChannel())
                    .routeToRecipients(r -> r
                            .recipient(outputAChannel(), (String s) -> s.contains("hi"))
                            .recipient(outputBChannel(), (String s) -> s.contains("bye")))
                    .get();
        }
    }
}
