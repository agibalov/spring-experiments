package me.loki2302;

import java.sql.Driver;
import javax.sql.DataSource;

import me.loki2302.repositories.UserRepository;

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
public class MySqlConfiguration extends AbstractConfiguration {    
    private final static String DATABASE_NAME = "JavaTestDb";
        
    @Bean
    @Override
    @SuppressWarnings("unchecked")
    public DataSource dataSource() throws ClassNotFoundException {
        Class<? extends Driver> mySqlDriver = 
                (Class<? extends Driver>)Class.forName("com.mysql.jdbc.Driver");
        
        SimpleDriverDataSource masterDataSource = new SimpleDriverDataSource();
        masterDataSource.setDriverClass(mySqlDriver);
        masterDataSource.setUrl("jdbc:mysql://win7dev-home/?user=root&password=qwerty");
            
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(masterDataSource);
                    
        jdbcTemplate.update(
                String.format("drop database if exists %s", DATABASE_NAME), 
                new MapSqlParameterSource());
        
        jdbcTemplate.update(
                String.format("create database if not exists %s", DATABASE_NAME), 
                new MapSqlParameterSource());
        
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(mySqlDriver);
        dataSource.setUrl(String.format("jdbc:mysql://win7dev-home/%s?user=root&password=qwerty", DATABASE_NAME));
        return dataSource;
    }

    @Override
    protected String getDialect() {
        return "org.hibernate.dialect.MySQL5Dialect";
    }       
}