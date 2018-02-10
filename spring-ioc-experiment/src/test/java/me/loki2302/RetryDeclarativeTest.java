package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RetryDeclarativeTest {
    @Autowired
    @Qualifier(Config.UNRELIABLE_SERVICE_3_BEAN_NAME)
    private UnreliableService unreliableService3;

    @Autowired
    @Qualifier(Config.UNRELIABLE_SERVICE_4_BEAN_NAME)
    private UnreliableService unreliableService4;

    @Test
    public void positiveScenario() {
        assertEquals("success!", unreliableService3.get());
    }

    @Test
    public void negativeScenario() {
        try {
            unreliableService4.get();
            fail();
        } catch (RuntimeException e) {
            assertEquals("fail 3", e.getMessage());
        }
    }

    public static class UnreliableService {
        private int i;
        private int successOnNthAttempt;

        public UnreliableService(int successOnNthAttempt) {
            this.successOnNthAttempt = successOnNthAttempt;
        }

        @Retryable(maxAttempts = 3)
        public String get() {
            ++i;
            if(i < successOnNthAttempt) {
                throw new RuntimeException("fail " + i);
            }
            return "success!";
        }
    }

    @Configuration
    @EnableRetry
    public static class Config {
        public final static String UNRELIABLE_SERVICE_3_BEAN_NAME = "unreliableService3";
        public final static String UNRELIABLE_SERVICE_4_BEAN_NAME = "unreliableService4";

        @Bean(UNRELIABLE_SERVICE_3_BEAN_NAME)
        public UnreliableService unreliableService3() {
            return new UnreliableService(3);
        }

        @Bean(UNRELIABLE_SERVICE_4_BEAN_NAME)
        public UnreliableService unreliableService4() {
            return new UnreliableService(4);
        }
    }
}
