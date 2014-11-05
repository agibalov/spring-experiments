package me.loki2302;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = AopTest.Config.class)
public class AopTest {
    @Autowired
    private DummyService dummyService;

    @Autowired
    private AuditService auditService;

    @Test
    public void canUseAopForAudit() {
        dummyService.hello();

        List<String> events = auditService.getEvents();
        assertEquals(2, events.size());
        assertEquals("before:hello", events.get(0));
        assertEquals("after:hello", events.get(1));

        dummyService.hellgateOpen();

        events = auditService.getEvents();
        assertEquals(4, events.size());
        assertEquals("before:hellgateOpen", events.get(2));
        assertEquals("after:hellgateOpen", events.get(3));

        dummyService.ping();

        events = auditService.getEvents();
        assertEquals(6, events.size());
        assertEquals("before:ping", events.get(4));
        assertEquals("after:ping", events.get(5));
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class Config {
        @Bean
        LogCallsAspect dummyAspect() {
            return new LogCallsAspect();
        }

        @Bean
        DummyService dummyService() {
            return new DummyService();
        }

        @Bean
        AuditService auditService() {
            return new AuditService();
        }
    }

    @Aspect
    public static class LogCallsAspect {
        @Autowired
        private AuditService auditService;

        @Around("execution(* hell*(..))")
        public Object aroundAnyPublicMethodWhoseNameStartsWithHell(ProceedingJoinPoint pjp) throws Throwable {
            String methodName = pjp.getSignature().getName();

            auditService.addEvent("before:" + methodName);
            Object result = pjp.proceed();
            auditService.addEvent("after:" + methodName);
            return result;
        }

        @Around("@annotation(me.loki2302.AopTest$AuditMe)")
        public Object aroundAnyMethodAnnotatedWithAuditMe(ProceedingJoinPoint pjp) throws Throwable {
            String methodName = pjp.getSignature().getName();

            auditService.addEvent("before:" + methodName);
            Object result = pjp.proceed();
            auditService.addEvent("after:" + methodName);
            return result;
        }
    }

    public static class DummyService {
        public void hello() {
            System.out.println("hello()");
        }

        public void hellgateOpen() {
            System.out.println("hellgateOpen()");
        }

        @AuditMe
        public void ping() {
            System.out.println("ping()");
        }
    }

    public static class AuditService {
        private final List<String> events = new ArrayList<String>();

        public void addEvent(String event) {
            events.add(event);
        }

        public List<String> getEvents() {
            return events;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface AuditMe {
    }
}
