package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
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
    private SingletonScopedBean singletonScopedBean;

    @Test
    public void shouldAllowMeToUseCustomScope() {
        OperationContext operationContext1 = new OperationContext("operation1");
        OperationContextHolder.setOperationContext(operationContext1);
        assertEquals("operation1", singletonScopedBean.getOperationName());

        OperationContext operationContext2 = new OperationContext("operation2");
        OperationContextHolder.setOperationContext(operationContext2);
        assertEquals("operation2", singletonScopedBean.getOperationName());

        OperationContextHolder.setOperationContext(operationContext1);
        assertEquals("operation1", singletonScopedBean.getOperationName());
    }

    @Configuration
    public static class Config {
        @Bean
        public SingletonScopedBean singletonScopedBean() {
            return new SingletonScopedBean();
        }

        @Bean
        @OperationScope
        public OperationScopedBean operationScopedBean() {
            return new OperationScopedBean();
        }

        @Bean
        public BeanFactoryPostProcessor beanFactoryPostProcessor() {
            return beanFactory ->
                    beanFactory.registerScope(OperationScopeImpl.NAME, new OperationScopeImpl());
        }
    }

    public static class OperationContext {
        private final String name;
        private final Map<String, Object> attributes = new HashMap<>();

        public OperationContext(String name) {
            this.name = name;
        }

        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        public void setAttribute(String name, Object value) {
            attributes.put(name, value);
        }

        public void removeAttribute(String name) {
            attributes.remove(name);
        }
    }

    public static class OperationContextHolder {
        private static OperationContext OPERATION_CONTEXT;

        public static void setOperationContext(OperationContext operationContext) {
            OPERATION_CONTEXT = operationContext;
        }

        public static OperationContext getOperationContext() {
            return OPERATION_CONTEXT;
        }
    }

    public static class OperationScopedBean {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Component
    public static class SingletonScopedBean {
        private final Logger LOGGER = LoggerFactory.getLogger(SingletonScopedBean.class);

        @Autowired
        private OperationScopedBean operationScopedBean;

        public String getOperationName() {
            return operationScopedBean.getName();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Scope(OperationScopeImpl.NAME)
    public @interface OperationScope {
        @AliasFor(annotation = Scope.class)
        ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
    }

    public static class OperationScopeImpl implements org.springframework.beans.factory.config.Scope {
        private final static Logger LOGGER = LoggerFactory.getLogger(OperationScopeImpl.class);

        public final static String NAME = "operation";

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            OperationContext operationContext = OperationContextHolder.getOperationContext();
            if(operationContext == null) {
                throw new RuntimeException();
            }

            Object obj = operationContext.getAttribute(name);
            if(obj == null) {
                obj = objectFactory.getObject();

                if(obj instanceof OperationScopedBean) {
                    OperationScopedBean operationScopedBean = (OperationScopedBean)obj;
                    operationScopedBean.setName(operationContext.name);
                }

                operationContext.setAttribute(name, obj);
            }

            return obj;
        }

        @Override
        public Object remove(String name) {
            LOGGER.info("remove() name={}", name);

            OperationContext operationContext = OperationContextHolder.getOperationContext();
            if(operationContext == null) {
                throw new RuntimeException();
            }

            Object obj = operationContext.getAttribute(name);
            if(obj != null) {
                operationContext.removeAttribute(name);
                return obj;
            }

            return null;
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
            // Optional, intentionally blank
        }

        @Override
        public Object resolveContextualObject(String key) {
            // Optional, intentionally null
            return null;
        }

        @Override
        public String getConversationId() {
            // Optional, intentionally null
            return null;
        }
    }
}
