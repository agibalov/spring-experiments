package me.loki2302;

import org.junit.Before;
import org.junit.Test;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RetryTemplateTest {
    private RetryTemplate retryTemplate;

    @Before
    public void makeRetryTemplate() {
        retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
    }

    @Test
    public void positiveScenario() throws Throwable {
        Supplier<String> supplier = makeSupplierThatSucceedsOnNthAttempt(3);
        String result = retryTemplate.execute((RetryCallback<String, Throwable>) context ->
                supplier.get());
        assertEquals("success!", result);
    }

    @Test
    public void negativeScenario() throws Throwable {
        Supplier<String> supplier = makeSupplierThatSucceedsOnNthAttempt(4);
        try {
            retryTemplate.execute((RetryCallback<String, Throwable>) context ->
                    supplier.get());
            fail();
        } catch (RuntimeException e) {
            assertEquals("fail 3", e.getMessage());
        }
    }

    private static Supplier<String> makeSupplierThatSucceedsOnNthAttempt(int n) {
        return new Supplier<String>() {
            private int i;

            @Override
            public String get() {
                ++i;
                if(i < n) {
                    throw new RuntimeException("fail " + i);
                }
                return "success!";
            }
        };
    }
}
