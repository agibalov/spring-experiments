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
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.endpoint.MethodInvokingMessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageSourceTest {
    @Autowired
    @Qualifier("output")
    private QueueChannel output;

    @Test
    public void messageSourceShouldBePollable() {
        assertEquals(0, (int)(Integer)output.receive().getPayload());
        assertEquals(1, (int)(Integer)output.receive().getPayload());
        assertEquals(2, (int)(Integer)output.receive().getPayload());
    }

    @Configuration
    @EnableIntegration
    public static class Config {
        @Bean(name = "output")
        public QueueChannel outputChannel() {
            return MessageChannels.queue("output").get();
        }

        @Bean
        public MessageSource<?> dummyMessageSource() {
            MethodInvokingMessageSource messageSource = new MethodInvokingMessageSource();
            messageSource.setObject(new AtomicInteger());
            messageSource.setMethodName("getAndIncrement");
            return messageSource;
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return IntegrationFlows.from(dummyMessageSource(), s -> s.poller(Pollers.fixedRate(100)))
                    .channel(outputChannel())
                    .get();
        }
    }
}
