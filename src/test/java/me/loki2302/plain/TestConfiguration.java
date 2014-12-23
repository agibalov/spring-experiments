package me.loki2302.plain;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = TestConfiguration.class)
@Import(TestNeo4jConfiguration.class)
public class TestConfiguration {
}