package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.transformer.GenericTransformer;

@SpringBootApplication
@IntegrationComponentScan
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Autowired
    private UpperCaseGateway upperCaseGateway;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(upperCaseGateway.uppercase("hello"));
    }

    @Bean
    public IntegrationFlow upperCaseFlow() {
        return new IntegrationFlow() {
            @Override
            public void configure(IntegrationFlowDefinition<?> flow) {
                flow.transform(new GenericTransformer<String, String>() {
                    @Override
                    public String transform(String source) {
                        return source.toUpperCase();
                    }
                });
            }
        };
    }

    @MessagingGateway
    public static interface UpperCaseGateway {
        @Gateway(requestChannel = "upperCaseFlow.input")
        String uppercase(String source);
    }
}
