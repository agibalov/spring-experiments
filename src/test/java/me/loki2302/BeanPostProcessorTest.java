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
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeanPostProcessorTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TheHandlerRepository theHandlerRepository;

    @Test
    public void canConfigureABeanBasedOnOtherBeans() {
        Integer sum = theHandlerRepository.<AddNumbersRequest, Integer>handle(new AddNumbersRequest(2, 3));
        assertEquals(5, (int) sum);

        Integer difference = theHandlerRepository.<SubNumbersRequest, Integer>handle(new SubNumbersRequest(2, 3));
        assertEquals(-1, (int) difference);
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
                Map<String, TheHandler> handlers = listableBeanFactory.getBeansOfType(TheHandler.class);
                Map<Class<?>, TheHandler> handlersByRequestTypes = handlers.values().stream()
                        .collect(Collectors.toMap(
                                handler -> handler.getRequestClass(),
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
        private Map<Class<?>, TheHandler> handlersByRequestTypes = new HashMap<>();

        public void add(Map<Class<?>, TheHandler> handlersByRequestTypes) {
            this.handlersByRequestTypes.putAll(handlersByRequestTypes);
        }

        public <TRequest, TResponse> TResponse handle(TRequest request) {
            TheHandler theHandler = handlersByRequestTypes.get(request.getClass());
            return (TResponse)theHandler.handle(request);
        }
    }

    public interface TheHandler<TRequest, TResponse> {
        Class<TRequest> getRequestClass();
        TResponse handle(TRequest request);
    }

    @Component
    public static class AddNumbersHandler implements TheHandler<AddNumbersRequest, Integer> {
        @Override
        public Class<AddNumbersRequest> getRequestClass() {
            return AddNumbersRequest.class;
        }

        @Override
        public Integer handle(AddNumbersRequest addNumbersRequest) {
            return addNumbersRequest.a + addNumbersRequest.b;
        }
    }

    @Component
    public static class SubNumbersHandler implements TheHandler<SubNumbersRequest, Integer> {
        @Override
        public Class<SubNumbersRequest> getRequestClass() {
            return SubNumbersRequest.class;
        }

        @Override
        public Integer handle(SubNumbersRequest subNumbersRequest) {
            return subNumbersRequest.a - subNumbersRequest.b;
        }
    }

    public static class AddNumbersRequest {
        public final int a;
        public final int b;

        public AddNumbersRequest(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class SubNumbersRequest {
        public final int a;
        public final int b;

        public SubNumbersRequest(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }
}
