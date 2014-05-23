package me.loki2302;

import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DummyTest {
    @Test
    public void dummy() throws MessagingException, IOException {
        Wiser wiser = new Wiser(25000);
        wiser.start();

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("localhost");
        javaMailSender.setPort(25000);

        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom("a@a.a");
        m.setTo("b@b.b");
        m.setSubject("hello");
        m.setText("Hi there!");

        javaMailSender.send(m);

        wiser.stop();

        List<WiserMessage> messageList = wiser.getMessages();
        assertEquals(1, messageList.size());

        WiserMessage message = messageList.get(0);
        assertEquals("a@a.a", message.getEnvelopeSender());
        assertEquals("b@b.b", message.getEnvelopeReceiver());
        MimeMessage mimeMessage = message.getMimeMessage();
        assertEquals("hello", mimeMessage.getSubject());
        assertTrue(((String) mimeMessage.getContent()).contains("Hi there!"));

    }
}
