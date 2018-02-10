package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomComponentAnnotationTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void canFindBeansByAnnotation() {
        Map<String, Object> usefulThings = applicationContext
                .getBeansWithAnnotation(SomethingUseful.class);
        assertEquals(2, usefulThings.size());

        Collection<Object> instances = usefulThings.values();
        instances.contains(applicationContext.getBean(Hammer.class));
        instances.contains(applicationContext.getBean(Nail.class));
    }

    @Configuration
    public static class Config {
        @Bean
        public Hammer hammer() {
            return new Hammer();
        }

        @Bean
        public Nail nail() {
            return new Nail();
        }
    }

    @Component
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SomethingUseful {
    }

    @SomethingUseful
    public static class Hammer {}

    @SomethingUseful
    public static class Nail {}
}
