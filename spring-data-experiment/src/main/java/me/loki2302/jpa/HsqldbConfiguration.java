package me.loki2302.jpa;

import javax.sql.DataSource;

import me.loki2302.jpa.repositories.UserRepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EnableTransactionManagement
public class HsqldbConfiguration extends AbstractConfiguration {
    @Override
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .setName("jpatestdb")
            .build();
    }

    @Override
    protected String getDialect() {
        return "org.hibernate.dialect.HSQLDialect";
    }           
}