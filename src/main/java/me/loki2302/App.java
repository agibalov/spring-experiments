package me.loki2302;

import com.googlecode.flyway.core.Flyway;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(AppConfiguration.class, args);
    }

    @Configuration
    @ComponentScan
    @EnableAutoConfiguration
    public static class AppConfiguration {
        @Bean
        DataSource dataSource(Server h2Server) {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriverClass(org.h2.Driver.class);
            dataSource.setUsername("sa");
            dataSource.setPassword("");
            //dataSource.setUrl("jdbc:h2:mem:test");
            dataSource.setUrl("jdbc:h2:tcp://localhost:9092/mem:test;DB_CLOSE_DELAY=-1");

            Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            flyway.migrate();

            return dataSource;
        }


        @Bean
        NamedParameterJdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean(name = "h2Server")
        Server h2Server() throws SQLException {
            // jdbc:h2:tcp://localhost:9092/mem:test;DB_CLOSE_DELAY=-1
            return Server.createTcpServer().start();
        }

        @Bean
        Server h2WebServer() throws SQLException {
            // http://localhost:8082/
            return Server.createWebServer().start();
        }
    }

    @Controller
    public static class HomeController {
        @RequestMapping("/")
        @ResponseBody
        String hello() {
            return "hello";
        }
    }
}
