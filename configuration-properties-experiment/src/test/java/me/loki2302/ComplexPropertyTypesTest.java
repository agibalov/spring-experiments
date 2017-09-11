package me.loki2302;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.istack.internal.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.config.location=classpath:omg.yml")
public class ComplexPropertyTypesTest {
    @Autowired
    private DummyProperties dummyProperties;

    @Test
    public void itShouldWork() throws IOException {
        Operation operation = dummyProperties.getOperation();
        assertEquals("CAT!!!", operation.apply("cat"));
        assertEquals("dog!!!", operation.apply("Dog"));
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public DummyProperties dummyProperties() {
            return new DummyProperties();
        }

        @Bean
        public OperationConverter durationConverter() {
            return new OperationConverter();
        }
    }

    @ConfigurationPropertiesBinding
    public static class OperationConverter implements Converter<String, Operation> {
        @Override
        public Operation convert(String source) {
            if(source == null) {
                return null;
            }

            try {
                return new ObjectMapper(new YAMLFactory()).readValue(source, Operation.class);
            } catch (IOException e) {
                return null;
            }
        }
    }

    @ConfigurationProperties("dummy")
    @Validated
    public static class DummyProperties {
        @NotNull
        private Operation operation;

        public Operation getOperation() {
            return operation;
        }

        public void setOperation(Operation operation) {
            this.operation = operation;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = StartsWithCondition.class, name = "starts-with")
    })
    public interface Condition {
        boolean test(String input);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SequenceOperation.class, name = "sequence"),
            @JsonSubTypes.Type(value = IfOperation.class, name = "if"),
            @JsonSubTypes.Type(value = ToUpperCaseOperation.class, name = "uppercase"),
            @JsonSubTypes.Type(value = ToLowerCaseOperation.class, name = "lowercase"),
            @JsonSubTypes.Type(value = AppendOperation.class, name = "append")
    })
    public interface Operation {
        String apply(String input);
    }

    public static class StartsWithCondition implements Condition {
        public String prefix;

        @Override
        public boolean test(String input) {
            return input.startsWith(prefix);
        }
    }

    public static class SequenceOperation implements Operation {
        public List<Operation> operations;

        @Override
        public String apply(String input) {
            String output = input;
            for(Operation operation : operations) {
                output = operation.apply(output);
            }
            return output;
        }
    }

    public static class IfOperation implements Operation {
        public Condition condition;
        public Operation yes;
        public Operation no;

        @Override
        public String apply(String input) {
            if(condition.test(input)) {
                return yes.apply(input);
            }

            return no.apply(input);
        }
    }

    public static class ToUpperCaseOperation implements Operation {
        @Override
        public String apply(String input) {
            return input.toUpperCase();
        }
    }

    public static class ToLowerCaseOperation implements Operation {
        @Override
        public String apply(String input) {
            return input.toLowerCase();
        }
    }

    public static class AppendOperation implements Operation {
        public String suffix;

        @Override
        public String apply(String input) {
            return input + suffix;
        }
    }
}
