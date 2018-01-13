package me.loki2302.servlet;

import me.loki2302.servlet.shared.SilentResponseErrorHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class BeanValidationTest {
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
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
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

    @Controller
    public static class HomeController {
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
        @NotEmpty
        public String username;
    }
}
