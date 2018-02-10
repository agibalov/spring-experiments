package me.loki2302.servlet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class EnumPathVariableViaCustomConverterTest {
    @Test
    public void checkCurrencies() {
        RestTemplate restTemplate = new RestTemplate();
        Assert.assertEquals("US Dollars", restTemplate.getForObject("http://localhost:8080/usd", String.class));
        Assert.assertEquals("Euros", restTemplate.getForObject("http://localhost:8080/eur", String.class));

        try {
            restTemplate.getForObject("http://localhost:8080/omg", String.class);
        } catch(HttpClientErrorException e) {
            Assert.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config extends WebMvcConfigurerAdapter {
        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new StringToCurrencyConverter());
        }

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

    public static class StringToCurrencyConverter implements Converter<String, Currency> {
        @Override
        public Currency convert(String source) {
            if(source.equals("usd")) {
                return Currency.USD;
            } else if(source.equals("eur")) {
                return Currency.EUR;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public enum Currency {
        USD,
        EUR
    }
}
