package me.loki2302.parameterized;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(Parameterized.class)
public class DummyTest {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private final int a;
    private final int b;
    private final int expected;

    public DummyTest(int a, int b, int expected) {
        this.a = a;
        this.b = b;
        this.expected = expected;
    }

    @Test
    public void addEndpointShouldAdd2Numbers() {
        RestTemplate restTemplate = new RestTemplate();
        String resultString = restTemplate.getForObject("http://localhost:8080/add/{a}/{b}", String.class, a, b);
        int actual = Integer.valueOf(resultString);
        assertEquals(expected, actual);
    }

    @Parameterized.Parameters(name = "{index}: {0} + {1} == {2}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
                { 1, 2, 3 },
                { 2, 3, 5 },
                { 11, 22, 33 }
        });
    }

    @SpringBootApplication
    public static class Config {
        @Bean
        public CalculatorController calculatorController() {
            System.out.println("FUCK!");
            return new CalculatorController();
        }
    }

    @RestController
    public static class CalculatorController {
        @RequestMapping(value = "/add/{a}/{b}", method = RequestMethod.GET)
        public String addNumbers(@PathVariable("a") Integer a, @PathVariable("b") Integer b) {
            return String.format("%d", a + b);
        }
    }
}
