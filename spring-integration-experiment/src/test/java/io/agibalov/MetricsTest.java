package io.agibalov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.support.management.MessageChannelMetrics;
import org.springframework.integration.support.management.PollableChannelManagement;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MetricsTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(MetricsTest.class);

    @Autowired
    @Qualifier("input")
    private DirectChannel input;

    @Autowired
    @Qualifier("output")
    private QueueChannel output;

    @Autowired
    private ApplicationContext context;

    @Test
    public void metricsShouldUpdate() {
        Map<String, MessageChannelMetrics> messageChannelMetricsMap = context.getBeansOfType(MessageChannelMetrics.class);
        messageChannelMetricsMap.entrySet().forEach(e ->
                LOGGER.info("(MessageChannelMetrics) {}: {}", e.getKey(), e.getValue().getSendCount()));
        Map<String, PollableChannelManagement> pollableChannelManagementMap = context.getBeansOfType(PollableChannelManagement.class);
        pollableChannelManagementMap.entrySet().forEach(e ->
                LOGGER.info("(PollableChannelManagement) {}: {}", e.getKey(), e.getValue().getReceiveCount()));

        input.send(MessageBuilder.withPayload("hello").build());

        assertEquals(1, input.getSendCount());
        assertEquals(1, messageChannelMetricsMap.get("input").getSendCount());

        assertEquals(0, output.getReceiveCount());
        assertEquals(0, pollableChannelManagementMap.get("output").getReceiveCount());

        output.receive(0);
        assertEquals(1, output.getReceiveCount());
        assertEquals(1, pollableChannelManagementMap.get("output").getReceiveCount());
    }

    @Configuration
    @EnableIntegration
    @EnableIntegrationManagement
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
                    .transform(String.class, s -> s.toUpperCase())
                    .channel(outputChannel())
                    .get();
        }
    }
}
