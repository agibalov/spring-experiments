package io.agibalov.tracing;

import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.sql.DataSource;
import java.util.stream.Collectors;

@Slf4j
public class DataSourceProxifyingBeanPostProcessor implements BeanPostProcessor {
    private final SqlTracer sqlTracer;

    public DataSourceProxifyingBeanPostProcessor(SqlTracer sqlTracer) {
        this.sqlTracer = sqlTracer;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(!(bean instanceof DataSource)) {
            return bean;
        }

        DataSource originalDataSource = (DataSource)bean;
        return ProxyDataSourceBuilder.create(originalDataSource)
                .beforeQuery((execInfo, queryInfoList) ->
                        queryInfoList.forEach(queryInfo -> {
                            String query = queryInfo.getQuery();
                            log.info("Query: {}", query);
                            log.info("Arguments: {}", queryInfo.getParametersList().stream()
                                    .flatMap(x -> x.stream())
                                    .map(x -> x.getArgs()[1])
                                    .collect(Collectors.toList()));

                            sqlTracer.addQuery(query);
                        }))
                .build();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
