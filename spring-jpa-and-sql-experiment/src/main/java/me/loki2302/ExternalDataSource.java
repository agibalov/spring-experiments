package me.loki2302;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("app")
public class ExternalDataSource {
    @Bean
    DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        config.setJdbcUrl("jdbc:postgresql://localhost/testdb");
        config.setUsername("postgres");
        config.setPassword("qwerty");
        config.setMaximumPoolSize(100);

        HikariDataSource hikariDataSource = new HikariDataSource(config);

        return hikariDataSource;
    }
}
