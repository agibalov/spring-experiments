package me.loki2302;

import java.sql.Driver;
import java.util.Properties;

import javax.sql.DataSource;

import me.loki2302.repositories.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EnableTransactionManagement
public class SqlServer2008Configuration {    
    private final static String DATABASE_NAME = "JavaTestDb";
    
    @Bean
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
    
    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        Properties jpaProperties = new Properties();        
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2008Dialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "create");
        
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan(new String[] { "me.loki2302" });
        entityManagerFactory.setPersistenceUnitName("MyPersistenceUnit");
        entityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter);        
        entityManagerFactory.setJpaProperties(jpaProperties);
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory;
    }       
}