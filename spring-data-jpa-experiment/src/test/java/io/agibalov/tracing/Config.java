package io.agibalov.tracing;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan
public class Config {
    @Bean
    public DataSourceProxifyingBeanPostProcessor myDataSourceBeanPostProcessor(SqlTracer sqlTracer) {
        return new DataSourceProxifyingBeanPostProcessor(sqlTracer);
    }

    @Bean
    public SqlTracer sqlTracer() {
        return new SqlTracer();
    }

}
