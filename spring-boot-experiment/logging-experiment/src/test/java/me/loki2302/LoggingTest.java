package me.loki2302;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LoggingTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoggingTest.class);

    @Test
    public void dummy() {
        LOGGER.info("this one is not included");

        SpyingFacade.reset();
        LOGGER.info("hello there");
        LOGGER.info("qwerty");

        List<LogRecord> logRecords = SpyingFacade.getRecords();
        logRecords.forEach(r -> System.out.printf("record: %s\n", r));

        assertEquals(2, logRecords.size());
        assertTrue(logRecords.stream().anyMatch(r -> r.message.contains("hello there")));
    }

    @Configuration
    public static class Config {
    }

    @Data
    public static class LogRecord {
        @JsonProperty("@timestamp")
        public String timestamp;

        @JsonProperty("logger_name")
        public String logger;

        public String level;
        public String message;
    }

    public static class SpyingFacade {
        private static String SPYING_APPENDER_NAME = "SPY";

        public static void reset() {
            SpyingAppender<ILoggingEvent> spyingAppender = getSpyingAppender();
            spyingAppender.reset();
        }

        public static List<LogRecord> getRecords() {
            SpyingAppender<ILoggingEvent> spyingAppender = getSpyingAppender();
            ByteArrayOutputStream byteArrayOutputStream = spyingAppender.getBaos();

            System.out.printf("JSON:\n%s\n", new String(byteArrayOutputStream.toByteArray()));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonFactory jsonFactory = new JsonFactory();
            byte[] jsonLines = byteArrayOutputStream.toByteArray();
            MappingIterator<LogRecord> logRecordIterator;
            try {
                logRecordIterator = objectMapper.readValues(
                        jsonFactory.createParser(jsonLines),
                        LogRecord.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                return logRecordIterator.readAll();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static SpyingAppender<ILoggingEvent> getSpyingAppender() {
            ch.qos.logback.classic.Logger logger =
                    ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
            SpyingAppender<ILoggingEvent> spyingAppender =
                    (SpyingAppender<ILoggingEvent>)logger.getAppender(SPYING_APPENDER_NAME);
            return spyingAppender;
        }
    }

    public static class SpyingAppender<E> extends OutputStreamAppender<E> {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        public ByteArrayOutputStream getBaos() {
            return baos;
        }

        public void reset() {
            baos.reset();
        }

        @Override
        public void start() {
            reset();
            setOutputStream(baos);
            super.start();
        }
    }
}
