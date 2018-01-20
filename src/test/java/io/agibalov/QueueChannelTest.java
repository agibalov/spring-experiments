package io.agibalov;

import org.junit.Test;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QueueChannelTest {
    @Test
    public void queueChannelShouldAllowToSendAndReceiveMessages() {
        QueueChannel queueChannel = MessageChannels.queue().get();

        Message<String> message = MessageBuilder.withPayload("hello").build();

        Message<String> m1 = (Message<String>)queueChannel.receive(Duration.ofSeconds(1).toMillis());
        assertNull(m1);

        queueChannel.send(message);

        Message<String> m2 = (Message<String>)queueChannel.receive(Duration.ofSeconds(1).toMillis());
        assertEquals(message, m2);
    }
}
