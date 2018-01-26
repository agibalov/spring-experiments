package io.agibalov;

import org.junit.Test;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.Assert.assertEquals;

public class ChannelInterceptorTest {
    @Test
    public void channelInterceptorShouldIntercept() {
        DirectChannel directChannel = MessageChannels.direct()
                .interceptor(new ChannelInterceptorAdapter() {
                    @Override
                    public Message<?> preSend(Message<?> message, MessageChannel channel) {
                        return MessageBuilder.fromMessage(message)
                                .setHeader("preSendMessage", "hi there")
                                .build();
                    }
                })
                .get();

        Message<?> messageHolder[] = { null };
        directChannel.subscribe(message -> messageHolder[0] = message);

        directChannel.send(MessageBuilder.withPayload("hello").build());

        Message<?> retrievedMessage = messageHolder[0];
        assertEquals("hello", retrievedMessage.getPayload());
        assertEquals("hi there", retrievedMessage.getHeaders().get("preSendMessage"));
    }
}
