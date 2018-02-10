package me.loki2302.jpa;

import java.sql.Driver;
import javax.sql.DataSource;

import me.loki2302.jpa.repositories.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EnableTransactionManagement
public class PostgreSqlConfiguration extends AbstractConfiguration {    
    private final static String DATABASE_NAME = "javatestdb";
    
    @Bean
    @Override
    @SuppressWarnings("unchecked")
    public DataSource dataSource() throws ClassNotFoundException {
        Class<? extends Driver> postgreSqlDriver = 
                (Class<? extends Driver>)Class.forName("org.postgresql.Driver");
        
        SimpleDriverDataSource masterDataSource = new SimpleDriverDataSource();
        masterDataSource.setDriverClass(postgreSqlDriver);
        masterDataSource.setUrl("jdbc:postgresql://win7dev-home/?user=postgres&password=qwerty");
            
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(masterDataSource);
        if(jdbcTemplate.queryForObject(
                "select count(*) from pg_database where datname = lower(:dbName)",
                new MapSqlParameterSource().addValue("dbName", DATABASE_NAME), 
                Integer.class) > 0) {
            
            jdbcTemplate.queryForObject(
                    String.format(
                            "select count(pg_terminate_backend(pid)) " +
                            "from pg_stat_activity " +
                            "where datname = lower(:dbName) and pid <> pg_backend_pid()"), 
                    new MapSqlParameterSource().addValue("dbName", DATABASE_NAME),
                    Integer.class);
            
            jdbcTemplate.update(
                    String.format("drop database %s", DATABASE_NAME), 
                    new MapSqlParameterSource());                
        }
            
        jdbcTemplate.update(
                String.format("create database %s", DATABASE_NAME), 
                new MapSqlParameterSource());
        
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(postgreSqlDriver);
        dataSource.setUrl(String.format("jdbc:postgresql://win7dev-home/%s?user=postgres&password=qwerty", DATABASE_NAME));
        return dataSource;
    }

    @Override
    protected String getDialect() {
        return "org.hibernate.dialect.PostgreSQL82Dialect";
    }       
}