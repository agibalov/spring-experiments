package me.loki2302.cypher;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = TestConfiguration.class)
@Import(TestNeo4jConfiguration.class)
public class TestConfiguration {
}
