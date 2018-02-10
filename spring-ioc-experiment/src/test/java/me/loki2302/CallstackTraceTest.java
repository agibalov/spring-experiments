package me.loki2302;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CallstackTraceTest {
    @Autowired
    private DummyService dummyService;

    @Autowired
    private TransactionTraceAspect transactionTraceAspect;

    @Test
    public void dummy() throws JsonProcessingException {
        dummyService.addNumbers(2, 3);

        TxInfo txInfo = transactionTraceAspect.getLastTxInfo();
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(txInfo));

        // DummyService::addNumbers
        //   NumberValidator::validateNumber
        //     BottomBoundaryValidator::isOk
        //     TopBoundaryValidator::isOk
        //   NumberValidator::validateNumber
        //     BottomBoundaryValidator::isOk
        //     TopBoundaryValidator::isOk

        assertEquals("DummyService", txInfo.className);
        assertEquals("addNumbers", txInfo.methodName);
        assertEquals(2, txInfo.components.size());

        assertEquals("NumberValidator", txInfo.components.get(0).className);
        assertEquals("validateNumber", txInfo.components.get(0).methodName);
        assertEquals(2, txInfo.components.get(0).components.size());

        assertEquals("BottomBoundaryValidator", txInfo.components.get(0).components.get(0).className);
        assertEquals("isOk", txInfo.components.get(0).components.get(0).methodName);
        assertEquals(0, txInfo.components.get(0).components.get(0).components.size());

        assertEquals("TopBoundaryValidator", txInfo.components.get(0).components.get(1).className);
        assertEquals("isOk", txInfo.components.get(0).components.get(1).methodName);
        assertEquals(0, txInfo.components.get(0).components.get(1).components.size());

        assertEquals("NumberValidator", txInfo.components.get(1).className);
        assertEquals("validateNumber", txInfo.components.get(1).methodName);
        assertEquals(2, txInfo.components.get(1).components.size());

        assertEquals("BottomBoundaryValidator", txInfo.components.get(1).components.get(0).className);
        assertEquals("isOk", txInfo.components.get(1).components.get(0).methodName);
        assertEquals(0, txInfo.components.get(1).components.get(0).components.size());

        assertEquals("TopBoundaryValidator", txInfo.components.get(1).components.get(1).className);
        assertEquals("isOk", txInfo.components.get(1).components.get(1).methodName);
        assertEquals(0, txInfo.components.get(1).components.get(1).components.size());
    }

    @Configuration
    @Import(AopAutoConfiguration.class)
    public static class Config {
        @Bean
        public TransactionTraceAspect transactionTraceAspect() {
            return new TransactionTraceAspect();
        }

        @Bean
        public DummyService dummyService() {
            return new DummyService();
        }

        @Bean
        public NumberValidator numberValidator() {
            return new NumberValidator();
        }

        @Bean
        public TopBoundaryValidator topBoundaryValidator() {
            return new TopBoundaryValidator();
        }

        @Bean
        public BottomBoundaryValidator bottomBoundaryValidator() {
            return new BottomBoundaryValidator();
        }
    }

    @Aspect
    @Component
    public static class TransactionTraceAspect {
        private TxInfo rootTxInfo;
        private Stack<TxInfo> txStack;

        public TxInfo getLastTxInfo() {
            return rootTxInfo;
        }

        @Around("execution(@me.loki2302.CallstackTraceTest$TransactionEntryPoint * *.*(..))")
        public Object traceTransaction(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            MethodSignature methodSignature = (MethodSignature)proceedingJoinPoint.getSignature();

            rootTxInfo = new TxInfo(
                    proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName(),
                    proceedingJoinPoint.getSignature().getName(),
                    methodSignature.getMethod().getAnnotation(TransactionEntryPoint.class).value());
            txStack = new Stack<>();
            txStack.push(rootTxInfo);

            Object result = proceedingJoinPoint.proceed();

            txStack.pop();
            txStack = null;

            return result;
        }

        @Around("execution(@me.loki2302.CallstackTraceTest$TransactionComponent * *.*(..))")
        public Object traceTransactionComponent(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            MethodSignature methodSignature = (MethodSignature)proceedingJoinPoint.getSignature();

            TxInfo txInfo = new TxInfo(
                    proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName(),
                    proceedingJoinPoint.getSignature().getName(),
                    methodSignature.getMethod().getAnnotation(TransactionComponent.class).value());

            txStack.peek().components.add(txInfo);
            txStack.push(txInfo);

            Object result = proceedingJoinPoint.proceed();

            txStack.pop();

            return result;
        }
    }

    public static class TxInfo {
        public final String className;
        public final String methodName;
        public final String comment;
        public final List<TxInfo> components = new ArrayList<>();

        public TxInfo(String className, String methodName, String comment) {
            this.className = className;
            this.methodName = methodName;
            this.comment = comment;
        }
    }

    @Component
    public static class DummyService {
        @Autowired
        private NumberValidator numberValidator;

        @TransactionEntryPoint("add two numbers")
        public int addNumbers(int a, int b) {
            numberValidator.validateNumber(a);
            numberValidator.validateNumber(b);
            return a + b;
        }
    }

    @Component
    public static class NumberValidator {
        @Autowired
        private BottomBoundaryValidator bottomBoundaryValidator;

        @Autowired
        private TopBoundaryValidator topBoundaryValidator;

        @TransactionComponent("check if value is ok")
        public boolean validateNumber(int x) {
            return bottomBoundaryValidator.isOk(x) && topBoundaryValidator.isOk(x);
        }
    }

    @Component
    public static class BottomBoundaryValidator {
        @TransactionComponent("check if value is not too small")
        public boolean isOk(int x) {
            return x >= -100;
        }
    }

    @Component
    public static class TopBoundaryValidator {
        @TransactionComponent("check if value is not too huge")
        public boolean isOk(int x) {
            return x >= -100;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TransactionEntryPoint {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TransactionComponent {
        String value();
    }
}
