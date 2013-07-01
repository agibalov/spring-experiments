package me.loki2302.transactions.implicit;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackageClasses = TestConfiguration.class)
@Import(TestNeo4jConfiguration.class)
@EnableTransactionManagement
public class TestConfiguration {
}