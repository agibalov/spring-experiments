package io.agibalov.audit;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// 1. @EnableJpaAuditing is always required
// 2. dateTimeProviderRef is required if one wants to override the default implementation
@SpringBootApplication
@EnableJpaRepositories
@EntityScan
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class Config {
}
