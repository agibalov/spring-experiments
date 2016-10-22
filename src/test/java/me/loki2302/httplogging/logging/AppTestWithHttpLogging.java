package me.loki2302.httplogging.logging;

import me.loki2302.httplogging.app.AppConfig;
import org.springframework.boot.test.context.SpringBootTest;
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
@Retention(RetentionPolicy.RUNTIME)
public @interface AppTestWithHttpLogging {
}
