package me.loki2302.httplogging.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class ExecutionProgressTestExecutionListener extends AbstractTestExecutionListener {
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        Logger logger = LoggerFactory.getLogger(testContext.getTestClass());
        logger.info("Starting execution of {}::{} ({})",
                testContext.getTestClass().getSimpleName(),
                testContext.getTestMethod().getName(),
                testContext.getTestInstance());
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        Logger logger = LoggerFactory.getLogger(testContext.getTestClass());
        logger.info("Finished execution of {}::{} ({})",
                testContext.getTestClass().getSimpleName(),
                testContext.getTestMethod().getName(),
                testContext.getTestInstance());
    }
}
