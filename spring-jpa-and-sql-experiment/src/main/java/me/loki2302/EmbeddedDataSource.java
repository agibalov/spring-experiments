package me.loki2302;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

@Configuration
@Profile("test")
public class EmbeddedDataSource {
    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .addScripts(
                        "enable-postgres-compatibility-for-hsqldb.sql",
                        "schema.sql")
                .build();
    }
}
