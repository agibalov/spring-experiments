package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@SpringApplicationConfiguration(classes = DummyTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DummyTest {
    @Autowired
    private UpperCaseGateway upperCaseGateway;

    @Test
    public void dummy() {
        assertEquals("HELLO", upperCaseGateway.uppercase("hello"));
    }

    @Configuration
    @EnableAutoConfiguration
    @IntegrationComponentScan
    public static class Config {
        @Bean
        IntegrationFlow upperCaseFlow() {
            return new IntegrationFlow() {
                @Override
                public void accept(IntegrationFlowDefinition<?> integrationFlowDefinition) {
                    integrationFlowDefinition.transform(new GenericTransformer<String, String>() {
                        @Override
                        public String transform(String source) {
                            return source.toUpperCase();
                        }
                    });
                }
            };
        }
    }

    @MessagingGateway
    public static interface UpperCaseGateway {
        @Gateway(requestChannel = "upperCaseFlow.input")
        String uppercase(String source);
    }
}
