package me.loki2302;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ParentContextTest {
    @Test
    public void dummy() {
        AnnotationConfigApplicationContext context1 = new AnnotationConfigApplicationContext(Config1.class);
        ServiceA context1ServiceA = context1.getBean(ServiceA.class);

        AnnotationConfigApplicationContext context2 = new AnnotationConfigApplicationContext(Config2.class);
        context2.setParent(context1);
        ServiceA context2ServiceA = context2.getBean(ServiceA.class);
        assertEquals(context1ServiceA, context2ServiceA);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context1 = new AnnotationConfigApplicationContext(Config1.class);
        System.out.printf("***\nbeans1: %s\n***\n", Arrays.stream(context1.getBeanDefinitionNames())
                .collect(Collectors.joining(",\n")));
        System.out.printf("context1: %s\n", context1.getBean(ServiceA.class));

        AnnotationConfigApplicationContext context2 = new AnnotationConfigApplicationContext(Config2.class);
        context2.setParent(context1);
        System.out.printf("***\nbeans2: %s\n***\n", Arrays.stream(context2.getBeanDefinitionNames())
                .collect(Collectors.joining(",\n")));

        System.out.printf("context2: %s\n", context2.getBean(ServiceA.class));
    }

    @Configuration
    public static class Config1 {
        @Bean
        public ServiceA serviceA() {
            return new ServiceA();
        }
    }

    @Configuration
    public static class Config2 {
        @Bean
        public ServiceB serviceB() {
            return new ServiceB();
        }
    }

    public static class ServiceA {
    }

    public static class ServiceB {
    }
}
