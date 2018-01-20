package me.loki2302.transformation;

import org.junit.Test;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DirectChannelTest {
    @Test
    public void directChannelSendShouldThrowWhenThereAreNoSubscribers() {
        DirectChannel directChannel = MessageChannels.direct().get();
        Message<String> message = MessageBuilder.withPayload("hello").build();

        try {
            directChannel.send(message);
            fail();
        } catch (MessageDeliveryException e) {
            // intentionally blank
        }
    }

    @Test
    public void directChannelSendShouldDeliverToAtMostOneSubscriber() {
        MessageHandler messageHandler1 = mock(MessageHandler.class);
        MessageHandler messageHandler2 = mock(MessageHandler.class);

        DirectChannel directChannel = MessageChannels.direct().get();
        directChannel.subscribe(messageHandler1);
        directChannel.subscribe(messageHandler2);

        Message<String> message = MessageBuilder.withPayload("hello").build();
        directChannel.send(message);

        verify(messageHandler1, times(1)).handleMessage(message);
        verify(messageHandler2, times(0)).handleMessage(message);
    }
}
