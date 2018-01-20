package me.loki2302.transformation;

import org.junit.Test;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class PublishSubscribeChannelTest {
    @Test
    public void publishSubscribeChannelSendShouldDeliverToAllSubscribers() {
        MessageHandler messageHandler1 = mock(MessageHandler.class);
        MessageHandler messageHandler2 = mock(MessageHandler.class);

        PublishSubscribeChannel publishSubscribeChannel = MessageChannels.publishSubscribe().get();
        publishSubscribeChannel.subscribe(messageHandler1);
        publishSubscribeChannel.subscribe(messageHandler2);

        Message<String> message = MessageBuilder.withPayload("hello").build();
        publishSubscribeChannel.send(message);

        verify(messageHandler1, times(1)).handleMessage(message);
        verify(messageHandler2, times(1)).handleMessage(message);
    }
}
