package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.PostConstruct;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);

        // GET http://localhost:8080/people to get a collection
        // POST http://localhost:8080/people to create a new one
    }

    @Configuration
    @EnableJpaRepositories
    @Import(MyRepositoryRestMvcConfiguration.class)
    @EnableAutoConfiguration
    @ComponentScan
    public static class Config {
    }

    @Configuration
    public static class MyRepositoryRestMvcConfiguration extends RepositoryRestMvcConfiguration {
        @Autowired
        private PersonValidator personValidator;

        @Override
        protected void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
            validatingListener.addValidator("beforeCreate", personValidator);
        }
    }

    @Service
    public static class PersonValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            System.out.println("xxx");
            return clazz == Person.class;
        }

        @Override
        public void validate(Object target, Errors errors) {
            System.out.println("yyy");
            ValidationUtils.rejectIfEmpty(errors, "name", "NAME_IS_EMPTY", "name should not be empty");
        }
    }

    @Service
    public static class MyEventListener extends AbstractRepositoryEventListener<Person> {
        @Override
        protected void onBeforeCreate(Person entity) {
            System.out.printf("Before create %s\n", entity);
        }

        @Override
        protected void onAfterCreate(Person entity) {
            System.out.printf("After create %s\n", entity);
        }
    }
}
