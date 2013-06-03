package me.loki2302.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = App.class)
@Import(AppNeo4jConfiguration.class)
public class AppConfiguration {
}