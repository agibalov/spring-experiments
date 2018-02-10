package me.loki2302.jpa;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class AbstractConfiguration {
    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", getDialect());
        jpaProperties.put("hibernate.hbm2ddl.auto", "create");
        
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan(new String[] { "me.loki2302.jpa" });
        entityManagerFactory.setPersistenceUnitName("MyPersistenceUnit");
        entityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter);        
        entityManagerFactory.setJpaProperties(jpaProperties);
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory;
    }
    
    
    @Bean
    public abstract DataSource dataSource() throws ClassNotFoundException;
    
    protected abstract String getDialect(); 
}