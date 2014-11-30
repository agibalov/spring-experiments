package me.loki2302;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

@Configuration
@Profile("app")
public class ExternalDataSource {
    @Bean
    DataSource dataSource() {
        return new SimpleDriverDataSource(
                new org.postgresql.Driver(),
                "jdbc:postgresql://localhost/testdb",
                "postgres",
                "qwerty");
    }
}
