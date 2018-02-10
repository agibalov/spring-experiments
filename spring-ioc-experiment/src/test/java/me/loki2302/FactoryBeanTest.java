package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FactoryBeanTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void calculatorFactoryBeanConstructsACalculator() {
        Calculator calculator = applicationContext.getBean(Calculator.class);
        assertEquals(5, calculator.addNumbers(2, 3));
    }

    @Configuration
    public static class Config {
        @Bean
        public CalculatorFactoryBean calculatorFactoryBean() {
            return new CalculatorFactoryBean();
        }
    }

    public static class CalculatorFactoryBean extends AbstractFactoryBean<Calculator> {
        @Override
        public Class<?> getObjectType() {
            return Calculator.class;
        }

        @Override
        protected Calculator createInstance() throws Exception {
            AddNumbersHandler addNumbersHandler = new AddNumbersHandler();
            Calculator calculator = new Calculator(addNumbersHandler);
            return calculator;
        }
    }

    public static class Calculator {
        private final AddNumbersHandler addNumbersHandler;

        public Calculator(AddNumbersHandler addNumbersHandler) {
            this.addNumbersHandler = addNumbersHandler;
        }

        public int addNumbers(int a, int b) {
            return addNumbersHandler.addNumbers(a, b);
        }
    }

    public static class AddNumbersHandler {
        public int addNumbers(int a, int b) {
            return a + b;
        }
    }
}
