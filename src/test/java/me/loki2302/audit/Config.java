package me.loki2302.audit;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.PersistenceUnit;

@Configuration
@ComponentScan

// 1. @EnableJpaAuditing is always required
// 2. dateTimeProviderRef is required if one wants to override the default implementation
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableAutoConfiguration
public class Config {
}
