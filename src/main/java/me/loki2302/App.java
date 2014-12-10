package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.transformer.GenericTransformer;

public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Config.class, args);
        try {
            UpperCaseGateway upperCaseGateway = context.getBean(UpperCaseGateway.class);
            System.out.println(upperCaseGateway.uppercase("hello"));
        } finally {
            context.close();
        }
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
