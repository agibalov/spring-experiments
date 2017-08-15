package me.loki2302;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PropertiesValidationTest {
    @Test
    public void canPassValidation() {
        DummyProperties dummyProperties = new SpringApplicationBuilder(Config.class)
                .properties("dummy.message=hello there")
                .run()
                .getBean(DummyProperties.class);
        assertEquals("hello there", dummyProperties.getMessage());
    }

    @Test
    public void canGetAMessageEmptyError() {
        try {
            SpringApplication.run(Config.class);
            fail();
        } catch (Throwable t) {
            assertTrue(t instanceof BeanCreationException);
            BeanCreationException beanCreationException = (BeanCreationException)t;
            assertEquals("dummyProperties", beanCreationException.getBeanName());
            assertTrue(beanCreationException.getCause() instanceof BindException);
            BindException bindException = (BindException)beanCreationException.getCause();
            assertEquals(1, bindException.getErrorCount());
            FieldError fieldError = bindException.getFieldError("message");
            assertEquals("message", fieldError.getField());
            assertEquals("dummy", fieldError.getObjectName());
            assertEquals("message.empty", fieldError.getCode());
        }
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public DummyProperties dummyProperties() {
            return new DummyProperties();
        }

        // TODO: what if I want to have many validators? what if they all come from different modules?
        @Bean(name = "configurationPropertiesValidator")
        public static Validator myValidator() {
            return new DummyPropertiesValidator();
        }
    }

    @ConfigurationProperties("dummy")
    public static class DummyProperties {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class DummyPropertiesValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return clazz == DummyProperties.class;
        }

        @Override
        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmpty(errors, "message", "message.empty");
        }
    }
}
