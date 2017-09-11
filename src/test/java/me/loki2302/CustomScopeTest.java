package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomScopeTest {
    @Autowired
    private MyOperationHandler myOperationHandler;

    @Autowired
    private OperationResponse operationResponse;

    @Test
    public void dummy() {
        OperationContext operationContext = new OperationContext();
        operationContext.set("rawRequestContent", "hello");
        OperationContextHolder.setOperationContext(operationContext);

        myOperationHandler.handleRequest();

        assertEquals("operation response for hello", operationResponse.getOutput());
        OperationContextHolder.reset();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Scope(OperationScopeImpl.SCOPE_NAME)
    public @interface OperationScope {
        @AliasFor(annotation = Scope.class)
        ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
    }

    @Configuration
    public static class Config {
        @Bean
        public static OperationScopeBeanFactoryPostProcessor operationScopeBeanFactoryPostProcessor() {
            return new OperationScopeBeanFactoryPostProcessor();
        }

        @Bean
        @OperationScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public OperationRequest request() {
            String input = (String)OperationContextHolder.getOperationContext().get("rawRequestContent");
            return new OperationRequest(input);
        }

        @Bean
        @OperationScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public OperationResponse response() {
            return new OperationResponse();
        }

        @Bean
        @OperationScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public MyOperationHandler myOperationHandler() {
            return new MyOperationHandler();
        }
    }

    public static class OperationScopeBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.registerScope(OperationScopeImpl.SCOPE_NAME, new OperationScopeImpl());
        }
    }

    public static class MyOperationHandler {
        @Autowired
        private OperationRequest operationRequest;

        @Autowired
        private OperationResponse operationResponse;

        public void handleRequest() {
            operationResponse.setOutput(String.format("operation response for %s", operationRequest.getInput()));
        }
    }

    public static class OperationScopeImpl implements org.springframework.beans.factory.config.Scope {
        public final static String SCOPE_NAME = "operation";

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            OperationContext operationContext = getOperationContextOrThrow();
            Object object = operationContext.get(name);
            if(object == null) {
                object = objectFactory.getObject();
                operationContext.set(name, object);
            }

            return object;
        }

        @Override
        public Object remove(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
            getOperationContextOrThrow();
        }

        @Override
        public Object resolveContextualObject(String key) {
            getOperationContextOrThrow();
            return null;
        }

        @Override
        public String getConversationId() {
            getOperationContextOrThrow();
            return null;
        }

        private static OperationContext getOperationContextOrThrow() {
            OperationContext operationContext = OperationContextHolder.getOperationContext();
            if(operationContext == null) {
                throw new IllegalStateException();
            }
            return operationContext;
        }
    }

    public static class OperationContextHolder {
        private static OperationContext operationContext;

        public static OperationContext getOperationContext() {
            return operationContext;
        }

        public static void setOperationContext(OperationContext operationContext) {
            OperationContextHolder.operationContext = operationContext;
        }

        public static void reset() {
            operationContext = null;
        }
    }

    public static class OperationContext {
        private final Map<String, Object> scopedObjects = new HashMap<>();

        public Object get(String name) {
            return scopedObjects.get(name);
        }

        public void set(String name, Object object) {
            scopedObjects.put(name, object);
        }
    }

    public static class OperationRequest {
        private final String input;

        public OperationRequest(String input) {
            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }

    public static class OperationResponse {
        private String output;

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }
    }
}
