package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebIntegrationTest
@SpringApplicationConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class EnumPathVariableViaDefaultConversionTest {
    @Test
    public void checkCurrencies() {
        RestTemplate restTemplate = new RestTemplate();
        assertEquals("US Dollars", restTemplate.getForObject("http://localhost:8080/USD", String.class));
        assertEquals("Euros", restTemplate.getForObject("http://localhost:8080/EUR", String.class));

        try {
            restTemplate.getForObject("http://localhost:8080/omg", String.class);
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public CurrencyController dummyController() {
            return new CurrencyController();
        }
    }

    @RestController
    public static class CurrencyController {
        @RequestMapping(value = "/{currency}", method = RequestMethod.GET)
        public String index(@PathVariable("currency") Currency currency) {
            if(currency.equals(Currency.USD)) {
                return "US Dollars";
            } else if(currency.equals(Currency.EUR)) {
                return "Euros";
            }

            return "should never happen";
        }
    }

    public enum Currency {
        USD,
        EUR
    }
}
