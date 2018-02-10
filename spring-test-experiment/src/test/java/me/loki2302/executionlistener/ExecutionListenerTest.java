package me.loki2302.executionlistener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(
        listeners = ExecutionListenerTest.TestNameUpdatingTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ExecutionListenerTest {
    @Autowired
    private TestNameHolder testNameHolder;

    @Test
    public void testNameHolderShouldHaveNameSetBeforeTestStart() {
        String expectedName = String.format("%s %s %s",
                ExecutionListenerTest.class.getName(),
                "testNameHolderShouldHaveNameSetBeforeTestStart",
                this);

        assertEquals(expectedName, testNameHolder.name);
    }

    public static class TestNameUpdatingTestExecutionListener extends AbstractTestExecutionListener {
        @Override
        public void beforeTestMethod(TestContext testContext) throws Exception {
            Class<?> testClass = testContext.getTestClass();
            Method testMethod = testContext.getTestMethod();
            Object testInstance = testContext.getTestInstance();

            ApplicationContext applicationContext = testContext.getApplicationContext();
            TestNameHolder testNameHolder = applicationContext.getBean(TestNameHolder.class);
            testNameHolder.name = String.format("%s %s %s", testClass.getName(), testMethod.getName(), testInstance);
        }
    }

    @Configuration
    public static class Config {
        @Bean
        public TestNameHolder testNameHolder() {
            return new TestNameHolder();
        }
    }

    public static class TestNameHolder {
        public String name;
    }
}
