package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicInjectionTest {
    @Autowired
    private SomethingToInjectByType somethingToInjectByType;

    @Qualifier("b")
    @Autowired
    private SomethingToInjectByName somethingToInjectByNameUsingQualifierAnnotation;

    @Resource(name = "b")
    private SomethingToInjectByName somethingToInjectByNameUsingResourceAnnotation;

    @Autowired
    private List<SomeHandler> someHandlerList;

    @Autowired
    private Map<String, SomeHandler> someHandlerMap;

    @Test
    public void canInjectBeanByType() {
        assertTrue(somethingToInjectByType instanceof SomethingToInjectByType);
    }

    @Test
    public void canInjectBeanByNameUsingQualifierAnnotation() {
        assertEquals("B", somethingToInjectByNameUsingQualifierAnnotation.comment);
    }

    @Test
    public void canInjectBeanByNameUsingResourceAnnotation() {
        assertEquals("B", somethingToInjectByNameUsingResourceAnnotation.comment);
    }

    @Test
    public void canInjectListOfBeansBasedOnInterface() {
        assertEquals(3, someHandlerList.size());
        assertTrue(someHandlerList.stream().anyMatch(h -> h instanceof SomeHandlerA));
        assertTrue(someHandlerList.stream().anyMatch(h -> h instanceof SomeHandlerB));
        assertTrue(someHandlerList.stream().anyMatch(h -> h instanceof SomeHandlerC));
    }

    @Test
    public void canInjectMapOfBeansBasedOnInterface() {
        assertEquals(3, someHandlerMap.size());
        assertTrue(someHandlerMap.get("someHandlerA") instanceof SomeHandlerA);
        assertTrue(someHandlerMap.get("someHandlerB") instanceof SomeHandlerB);
        assertTrue(someHandlerMap.get("someHandlerC") instanceof SomeHandlerC);
    }

    @Configuration
    public static class Config {
        @Bean
        public SomethingToInjectByType something() {
            return new SomethingToInjectByType();
        }

        @Bean(name = "a")
        public SomethingToInjectByName somethingToInjectByNameA() {
            return new SomethingToInjectByName("A");
        }

        @Bean(name = "b")
        public SomethingToInjectByName somethingToInjectByNameB() {
            return new SomethingToInjectByName("B");
        }

        @Bean
        public SomeHandlerA someHandlerA() {
            return new SomeHandlerA();
        }

        @Bean
        public SomeHandlerB someHandlerB() {
            return new SomeHandlerB();
        }

        @Bean
        public SomeHandlerC someHandlerC() {
            return new SomeHandlerC();
        }
    }

    public static class SomethingToInjectByType {
    }

    public static class SomethingToInjectByName {
        public final String comment;

        public SomethingToInjectByName(String comment) {
            this.comment = comment;
        }
    }

    public interface SomeHandler {}
    public static class SomeHandlerA implements SomeHandler {}
    public static class SomeHandlerB implements SomeHandler {}
    public static class SomeHandlerC implements SomeHandler {}
}
