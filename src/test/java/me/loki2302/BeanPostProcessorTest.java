package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeanPostProcessorTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TheHandlerRepository theHandlerRepository;

    @Test
    public void canConfigureABeanBasedOnOtherBeans() {
        assertTrue(theHandlerRepository.getHandlerForRequestType(AddNumbersRequest.class) instanceof AddNumbersHandler);
        assertTrue(theHandlerRepository.getHandlerForRequestType(SubNumbersRequest.class) instanceof SubNumbersHandler);
    }

    @Configuration
    public static class Config {
        @Bean
        public TheHandlerRepository theHandlerRepository() {
            return new TheHandlerRepository();
        }

        @Bean
        public MyBeanPostProcessor myBeanPostProcessor() {
            return new MyBeanPostProcessor();
        }

        @Bean
        public AddNumbersHandler addNumbersHandler() {
            return new AddNumbersHandler();
        }

        @Bean
        public SubNumbersHandler subNumbersHandler() {
            return new SubNumbersHandler();
        }
    }

    public static class MyBeanPostProcessor implements BeanPostProcessor {
        private final static Logger LOGGER = LoggerFactory.getLogger(MyBeanPostProcessor.class);

        @Autowired
        private ListableBeanFactory listableBeanFactory;

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if(bean instanceof TheHandlerRepository) {
                Map<String, Object> handlers = listableBeanFactory.getBeansWithAnnotation(TheHandler.class);

                Map<Class<?>, Object> handlersByRequestTypes = handlers.values().stream()
                        .collect(Collectors.toMap(
                                handler -> AnnotationUtils.getAnnotation(
                                        handler.getClass(),
                                        TheHandler.class).value(),
                                handler -> handler));

                LOGGER.info("Handlers: {}", handlersByRequestTypes);

                ((TheHandlerRepository)bean).add(handlersByRequestTypes);
            }

            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }

    public static class TheHandlerRepository {
        private Map<Class<?>, Object> handlersByRequestTypes = new HashMap<>();

        public void add(Map<Class<?>, Object> handlersByRequestTypes) {
            this.handlersByRequestTypes.putAll(handlersByRequestTypes);
        }

        public Object getHandlerForRequestType(Class<?> requestTypeClass) {
            return handlersByRequestTypes.get(requestTypeClass);
        }
    }

    @Component
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TheHandler {
        Class<?> value();
    }

    @TheHandler(AddNumbersRequest.class)
    public static class AddNumbersHandler {}

    @TheHandler(SubNumbersRequest.class)
    public static class SubNumbersHandler {}

    public static class AddNumbersRequest {}
    public static class SubNumbersRequest {}
}
