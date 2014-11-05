package me.loki2302;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = AopSpELTest.Config.class)
public class AopSpELTest {
    @Autowired
    private DummyService dummyService;

    @Autowired
    private SecurityService securityService;

    @Test
    public void canUseAopForSecurity() {
        securityService.allowHello();
        dummyService.hello();

        securityService.disallowHello();
        try {
            dummyService.hello();
            fail("Managed to call hello(), though securityService has been set to not allow it");
        } catch(SecurityException e) {
            // expected
        }
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class Config {
        @Bean
        CheckSecurityBeforeCallAspect dummyAspect() {
            return new CheckSecurityBeforeCallAspect();
        }

        @Bean
        DummyService dummyService() {
            return new DummyService();
        }

        @Bean(name = "securityService")
        SecurityService securityService() {
            return new SecurityService();
        }
    }

    @Aspect
    public static class CheckSecurityBeforeCallAspect implements BeanFactoryAware {
        private BeanFactory beanFactory;

        @Around("@annotation(me.loki2302.AopSpELTest$SecureMe)")
        public Object aroundAnyMethodAnnotatedWithSecureMe(ProceedingJoinPoint pjp) throws Throwable {
            MethodSignature methodSignature = (MethodSignature)pjp.getSignature();
            Method method = methodSignature.getMethod();
            SecureMe secureMeAnnotation = method.getAnnotation(SecureMe.class);
            if(secureMeAnnotation == null) {
                throw new RuntimeException("Method " + method.getName() + " is supposed to be annotated with " + SecureMe.class.getName() + " annotation");
            }

            ExpressionParser expressionParser = new SpelExpressionParser();
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
            Expression expression = expressionParser.parseExpression(secureMeAnnotation.value());
            Boolean allowOrNull = expression.getValue(evaluationContext, Boolean.class);
            if(allowOrNull == null) {
                throw new RuntimeException(SecureMe.class.getName() + " expression is not expected to return null");
            }

            boolean allow = allowOrNull;
            if(!allow) {
                throw new SecurityException();
            }

            Object result = pjp.proceed();

            return result;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }

    public static class DummyService {
        @SecureMe("@securityService.canDoHello()")
        public void hello() {
            System.out.println("hello()");
        }
    }

    public static class SecurityService {
        private boolean allowHello = false;

        public void allowHello() {
            allowHello = true;
        }

        public void disallowHello() {
            allowHello = false;
        }

        public boolean canDoHello() {
            return allowHello;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface SecureMe {
        String value();
    }

    public static class SecurityException extends RuntimeException {
    }
}
