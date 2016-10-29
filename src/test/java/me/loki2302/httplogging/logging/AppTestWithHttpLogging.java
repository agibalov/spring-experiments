package me.loki2302.httplogging.logging;

import me.loki2302.httplogging.app.AppConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringBootTest(
        classes = { AppConfig.class, LoggingRestTemplateConfiguration.class },
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestExecutionListeners(
        listeners = ExecutionProgressTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppTestWithHttpLogging {
}
