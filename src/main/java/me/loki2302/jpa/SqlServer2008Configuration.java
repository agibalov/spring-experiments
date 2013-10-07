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
public class SqlServer2008Configuration extends AbstractConfiguration {    
    private final static String DATABASE_NAME = "JavaTestDb";
    
    @Bean
    @Override
    @SuppressWarnings("unchecked")
    public DataSource dataSource() throws ClassNotFoundException {
        Class<? extends Driver> sqlServerDriver = 
                (Class<? extends Driver>)Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        SimpleDriverDataSource masterDataSource = new SimpleDriverDataSource();
        masterDataSource.setDriverClass(sqlServerDriver);
        masterDataSource.setUrl("jdbc:sqlserver://win7dev-home;user=sa;password=qwerty;database=master;");
            
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(masterDataSource);
        if(jdbcTemplate.queryForObject(
                "select count(*) from master.dbo.sysdatabases where name = :dbName", 
                new MapSqlParameterSource().addValue("dbName", DATABASE_NAME), 
                Integer.class) > 0) {
                
            jdbcTemplate.update(
                    String.format("drop database %s", DATABASE_NAME), 
                    new MapSqlParameterSource());                
        }
            
        jdbcTemplate.update(
                String.format("create database %s", DATABASE_NAME), 
                new MapSqlParameterSource());
        
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(sqlServerDriver);
        dataSource.setUrl(String.format("jdbc:sqlserver://win7dev-home;user=sa;password=qwerty;database=%s;", DATABASE_NAME));
        return dataSource;
    }

    @Override
    protected String getDialect() {
        return "org.hibernate.dialect.SQLServer2008Dialect";
    }       
}