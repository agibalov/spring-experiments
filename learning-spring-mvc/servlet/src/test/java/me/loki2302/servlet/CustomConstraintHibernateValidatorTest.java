package me.loki2302.servlet;

import me.loki2302.servlet.shared.SilentResponseErrorHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.*;
import javax.validation.constraints.NotEmpty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class CustomConstraintHibernateValidatorTest {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void apiReturns200WhenRequestIsValid() {
        UserNameDto userNameDto = new UserNameDto();
        userNameDto.username = "Andrey";

        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        "http://localhost:8080/",
                        userNameDto,
                        String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void apiReturns400WhenRequestIsInvalid() {
        UserNameDto userNameDto = new UserNameDto();
        userNameDto.username = "John";

        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        "http://localhost:8080/",
                        userNameDto,
                        String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public Validator validator(ApplicationContext context) {
            LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
            localValidatorFactoryBean.setConstraintValidatorFactory(new SpringContextConstraintValidatorFactory(context));
            localValidatorFactoryBean.afterPropertiesSet();
            return localValidatorFactoryBean.getValidator();
        }

        @Bean
        @Scope("prototype")
        public GoodNameConstraintValidator goodNameConstraintValidator() {
            return new GoodNameConstraintValidatorImpl();
        }

        @Bean
        public HomeController homeController() {
            return new HomeController();
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new SilentResponseErrorHandler());
            return restTemplate;
        }
    }

    public static class SpringContextConstraintValidatorFactory implements ConstraintValidatorFactory {
        private final ApplicationContext context;

        public SpringContextConstraintValidatorFactory(ApplicationContext context) {
            this.context = context;
        }

        @Override
        public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
            try {
                return context.getBean(key);
            } catch (NoSuchBeanDefinitionException e) {
                // NOTE: if I provide my own factory, I'm responsible for constructing ALL
                // validators, not just the custom ones. So, if context doesn't know how to construct
                // something, I just stick to new instance with parameterless constructor.
                try {
                    return key.newInstance();
                } catch (InstantiationException e1) {
                    throw new RuntimeException(e1);
                } catch (IllegalAccessException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

        @Override
        public void releaseInstance(ConstraintValidator<?, ?> instance) {
        }
    }

    @Controller
    public static class HomeController {
        @RequestMapping(value = "/", method = RequestMethod.POST)
        @ResponseBody
        @ResponseStatus(HttpStatus.OK)
        public String hello(@Validated @RequestBody UserNameDto userNameDto) {
            return "hello there!";
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseBody
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public String validationError(MethodArgumentNotValidException e) {
            return "error";
        }
    }

    public static class UserNameDto {
        @NotEmpty
        @GoodName
        public String username;
    }

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = GoodNameConstraintValidator.class)
    @interface GoodName {
        String message() default "omg";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static interface GoodNameConstraintValidator extends ConstraintValidator<GoodName, String> {
    }

    public static class GoodNameConstraintValidatorImpl implements GoodNameConstraintValidator {
        @Override
        public void initialize(GoodName constraintAnnotation) {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value != null && value.equals("Andrey");
        }
    }
}
