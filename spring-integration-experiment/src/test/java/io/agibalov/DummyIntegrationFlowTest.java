package io.agibalov;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DummyIntegrationFlowTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(DummyIntegrationFlowTest.class);

    @Autowired
    @Qualifier("input")
    private DirectChannel inputChannel;

    @Autowired
    @Qualifier("output")
    private DirectChannel outputChannel;

    @Test
    public void dummyTest() {
        List<String> results = new ArrayList<>();
        outputChannel.subscribe(message -> {
            LOGGER.info("Got result! {}", message);
            results.add((String)message.getPayload());
        });

        inputChannel.send(new GenericMessage<>("{\n" +
                "  \"records\": [\n" +
                "    { \"something\": \"hello\" },\n" +
                "    { \"something\": \"-1\" },\n" +
                "    { \"something\": \"world\" }\n" +
                "  ]\n" +
                "}"));

        assertEquals(2, results.size());
        assertEquals("Hey! hello", results.get(0));
        assertEquals("Hey! world", results.get(1));
    }

    @Configuration
    @EnableIntegration
    public static class Config {
        @Bean(name = "input")
        public DirectChannel inputChannel() {
            return MessageChannels.direct().get();
        }

        @Bean(name = "output")
        public DirectChannel outputChannel() {
            return MessageChannels.direct().get();
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return IntegrationFlows.from(inputChannel())
                    .transform((String json) -> {
                        LOGGER.info("Got JSON: {}", json);
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            return objectMapper.readValue(json, KinesisEvent.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .split(KinesisEvent.class, kinesisEvent -> {
                        LOGGER.info("Got KinesisEvent to extract {} records", kinesisEvent.records.size());
                        return kinesisEvent.records;
                    })
                    .transform(KinesisEventRecord.class, kinesisEventRecord -> {
                        LOGGER.info("Got KinesisEventRecord to convert: {}", kinesisEventRecord);

                        UserVisitEvent userVisitEvent = new UserVisitEvent();
                        userVisitEvent.userId = kinesisEventRecord.something;
                        return userVisitEvent;
                    })
                    .filter(UserVisitEvent.class, userVisitEvent -> {
                        LOGGER.info("Got UserVisitEvent to filter: {}", userVisitEvent);

                        if(userVisitEvent.userId.equals("-1")) {
                            return false;
                        }

                        return true;
                    })
                    .handle(UserVisitEvent.class, (userVisitEvent, headers) -> {
                        LOGGER.info("Got message to handle: {}", userVisitEvent);
                        return "Hey! " + userVisitEvent.userId;
                    })
                    .channel(outputChannel())
                    .get();
        }
    }

    public static class KinesisEvent {
        public List<KinesisEventRecord> records;
    }

    public static class KinesisEventRecord {
        public String something;
    }

    public static class UserVisitEvent {
        public String userId;
    }
}
