package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = BeanFactoryPostProcessorTest.Config.class)
public class BeanFactoryPostProcessorTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void defineCalculatorBeanFactoryPostProcessorDefinesACalculator() {
        Calculator calculator = applicationContext.getBean("calculator", Calculator.class);
        assertEquals(5, calculator.addNumbers(2, 3));
    }

    @Configuration
    public static class Config {
        @Bean
        public DefineCalculatorBeanFactoryPostProcessor dummyBeanFactoryPostProcessor() {
            return new DefineCalculatorBeanFactoryPostProcessor();
        }
    }

    public static class DefineCalculatorBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(Calculator.class);
            beanDefinition.setLazyInit(false);
            beanDefinition.setAbstract(false);
            beanDefinition.setAutowireCandidate(false);
            beanDefinition.setScope("singleton");
            ((BeanDefinitionRegistry)beanFactory).registerBeanDefinition("calculator", beanDefinition);
        }
    }

    public static class Calculator {
        public int addNumbers(int a, int b) {
            return a + b;
        }
    }
}
