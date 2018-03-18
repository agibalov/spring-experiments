package me.loki2302;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MethodValidationTest {
    @Autowired
    private MessageProcessor messageProcessor;

    @Test
    public void shouldThrowIfValidationFails() {
        Message message = new Message();
        message.data = null;

        try {
            messageProcessor.process(message);
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            assertEquals(1, violations.size());

            ConstraintViolation<?> violation = violations.iterator().next();
            assertEquals("process.message.data", violation.getPropertyPath().toString());
            assertEquals("may not be empty", violation.getMessage());
        }
    }

    @Test
    public void shouldNotThrowIfValidationDoesNotFail() {
        Message message = new Message();
        message.data = "hello";
        String result = messageProcessor.process(message);
        assertEquals("HELLO", result);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public MessageProcessor messageProcessor() {
            return new MessageProcessor();
        }
    }

    @Validated
    public static class MessageProcessor {
        public String process(@Valid Message message) {
            return message.data.toUpperCase();
        }
    }

    public static class Message {
        @NotEmpty
        public String data;
    }
}
