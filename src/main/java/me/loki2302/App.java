package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        // GET http://localhost:8080/people to get a collection
        // POST http://localhost:8080/people to create a new one
    }

    @Configuration
    public static class MyRepositoryRestConfigurer extends RepositoryRestConfigurerAdapter {
        @Autowired
        private PersonValidator personValidator;

        @Override
        public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
            validatingListener.addValidator("beforeCreate", personValidator);
        }

        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
            config.setBasePath("/api");
        }
    }

    @Component
    public static class PersonValidator implements Validator {
        private final static Logger LOGGER = LoggerFactory.getLogger(PersonValidator.class);

        @Override
        public boolean supports(Class<?> clazz) {
            LOGGER.info("{}", clazz);
            return clazz == Person.class;
        }

        @Override
        public void validate(Object target, Errors errors) {
            LOGGER.info("{}", target);
            ValidationUtils.rejectIfEmpty(errors, "name", "NAME_IS_EMPTY", "name should not be empty");
        }
    }

    @Component
    @RepositoryEventHandler
    public static class MyRepositoryEventHandler {
        private final static Logger LOGGER = LoggerFactory.getLogger(MyRepositoryEventHandler.class);

        @HandleBeforeCreate
        public void handleBeforeEntityCreate(Object e) {
            LOGGER.info("Before create {}", e);
        }

        @HandleAfterCreate
        public void handleAfterEntityCreate(Object e) {
            LOGGER.info("After create {}", e);
        }
    }
}
