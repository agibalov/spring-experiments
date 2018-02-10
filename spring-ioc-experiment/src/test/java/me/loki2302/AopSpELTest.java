package me.loki2302;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AopSpELTest {
    @Autowired
    private DummyService dummyService;

    @Autowired
    private SecurityService securityService;

    @Test
    public void canUseAopForSecurity() {
        dummyService.hello("loki2302");

        try {
            dummyService.hello("Andrey");
            fail("Managed to call hello() with name other than loki2302");
        } catch(SecurityException e) {
            // expected
        }
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class Config {
        @Bean
        CheckSecurityBeforeCallAspect checkSecurityBeforeCallAspect() {
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
        private final static Logger LOGGER = LoggerFactory.getLogger(CheckSecurityBeforeCallAspect.class);

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

            String[] parameterNames = methodSignature.getParameterNames();
            Object[] arguments = pjp.getArgs();
            for(int i = 0; i < parameterNames.length; ++i) {
                String parameterName = parameterNames[i];
                Object argument = arguments[i];
                evaluationContext.setVariable(parameterName, argument);
            }

            Expression expression = expressionParser.parseExpression(secureMeAnnotation.value());

            LOGGER.info("Checking if it's OK to call {} {}", pjp.getTarget(), method.getName());

            Boolean allowOrNull = expression.getValue(evaluationContext, Boolean.class);
            if(allowOrNull == null) {
                throw new RuntimeException(SecureMe.class.getName() + " expression is not expected to return null");
            }

            boolean allow = allowOrNull;
            if(!allow) {
                LOGGER.info("They say it's not OK to call {} {}", pjp.getTarget(), method.getName());
                throw new SecurityException();
            }

            LOGGER.info("They say it's OK to call {} {}", pjp.getTarget(), method.getName());
            Object result = pjp.proceed();

            return result;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }

    public static class DummyService {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyService.class);

        @SecureMe("@securityService.canSayHello(#name)")
        public void hello(String name) {
            LOGGER.info("Hello, {}!", name);
        }
    }

    public static class SecurityService {
        private final static Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

        public boolean canSayHello(String name) {
            LOGGER.info("Someone asks if it's OK to say 'hello' to '{}'", name);
            boolean canSayHello = name != null && name.equals("loki2302");
            if(canSayHello) {
                LOGGER.info("It's indeed OK");
            } else {
                LOGGER.info("No, it's not OK");
            }

            return canSayHello;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface SecureMe {
        String value();
    }

    public static class SecurityException extends RuntimeException {
    }
}
