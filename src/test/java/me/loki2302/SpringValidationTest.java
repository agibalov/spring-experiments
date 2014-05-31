package me.loki2302;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = SpringValidationTest.Config.class)
@WebAppConfiguration
@DirtiesContext
public class SpringValidationTest {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void apiReturns200WhenRequestIsValid() {
        UserNameDto userNameDto = new UserNameDto();
        userNameDto.username = "loki2302";

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
        userNameDto.username = "";

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
        public UserNameDtoValidator userNameDtoValidator() {
            return new UserNameDtoValidator();
        }

        @Bean
        public HomeController homeController() {
            return new HomeController();
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new NonThrowingResponseErrorHandler());
            return restTemplate;
        }
    }

    @Controller
    public static class HomeController {
        @Autowired
        private UserNameDtoValidator userNameDtoValidator;

        @InitBinder
        private void initBinder(WebDataBinder webDataBinder) {
            webDataBinder.addValidators(userNameDtoValidator);
        }

        @RequestMapping(value = "/", method = RequestMethod.POST)
        @ResponseBody
        @ResponseStatus(HttpStatus.OK)
        public String hello(@Valid @RequestBody UserNameDto userNameDto) {
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
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class UserNameDtoValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return clazz.equals(UserNameDto.class);
        }

        @Override
        public void validate(Object target, Errors errors) {
            UserNameDto userNameDto = (UserNameDto)target;
            ValidationUtils.rejectIfEmpty(errors, "username", "required");
        }
    }

    private static class NonThrowingResponseErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
        }
    }
}
