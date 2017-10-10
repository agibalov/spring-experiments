package me.loki2302;

import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class EmbeddedTomcatJndiDataSourceTest {
    @Test
    public void ping() {
        RestTemplate restTemplate = new RestTemplate();
        assertEquals(
                "ds1 says: mem:databaseone, ds2 says: mem:databasetwo",
                restTemplate.getForObject("http://localhost:8080/", String.class));
    }

    @Configuration
    @EnableAutoConfiguration(exclude = { // disable entirely Spring Boot JPA
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            JpaRepositoriesAutoConfiguration.class
    })
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Bean
        public DataSource dataSourceOne() throws NamingException {
            JndiTemplate jndiTemplate = new JndiTemplate();
            return jndiTemplate.lookup("java:comp/env/dataSourceOne", DataSource.class);
        }

        @Bean
        public DataSource dataSourceTwo() throws NamingException {
            JndiTemplate jndiTemplate = new JndiTemplate();
            return jndiTemplate.lookup("java:comp/env/dataSourceTwo", DataSource.class);
        }

        @Bean
        public JdbcTemplate dataSourceOneJdbcTemplate(@Qualifier("dataSourceOne") DataSource dataSourceOne) throws NamingException {
            return new JdbcTemplate(dataSourceOne);
        }

        @Bean
        public JdbcTemplate dataSourceTwoJdbcTemplate(@Qualifier("dataSourceTwo") DataSource dataSourceTwo) throws NamingException {
            return new JdbcTemplate(dataSourceTwo);
        }

        @Bean
        public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
            TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory() {
                @Override
                protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                    tomcat.enableNaming();
                    return super.getTomcatWebServer(tomcat);
                }
            };

            tomcatServletWebServerFactory.addContextCustomizers((TomcatContextCustomizer) context -> {
                ContextResource dataSourceOneContextResource = new ContextResource();
                dataSourceOneContextResource.setName("dataSourceOne");
                dataSourceOneContextResource.setType(DataSource.class.getName());
                dataSourceOneContextResource.setProperty("driverClassName", JDBCDriver.class.getName());
                dataSourceOneContextResource.setProperty("url", "jdbc:hsqldb:mem:databaseOne");
                dataSourceOneContextResource.setProperty("username", "sa");
                context.getNamingResources().addResource(dataSourceOneContextResource);

                ContextResource dataSourceTwoContextResource = new ContextResource();
                dataSourceTwoContextResource.setName("dataSourceTwo");
                dataSourceTwoContextResource.setType(DataSource.class.getName());
                dataSourceTwoContextResource.setProperty("driverClassName", JDBCDriver.class.getName());
                dataSourceTwoContextResource.setProperty("url", "jdbc:hsqldb:mem:databaseTwo");
                dataSourceTwoContextResource.setProperty("username", "sa");
                context.getNamingResources().addResource(dataSourceTwoContextResource);
            });

            return tomcatServletWebServerFactory;
        }
    }

    @RestController
    public static class DummyController {
        @Autowired
        @Qualifier("dataSourceOneJdbcTemplate")
        private JdbcTemplate dataSourceOneJdbcTemplate;

        @Autowired
        @Qualifier("dataSourceTwoJdbcTemplate")
        private JdbcTemplate dataSourceTwoJdbcTemplate;

        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String index() {
            return "ds1 says: " +
                    getDatabaseName(dataSourceOneJdbcTemplate) +
                    ", ds2 says: " +
                    getDatabaseName(dataSourceTwoJdbcTemplate);
        }

        private static String getDatabaseName(JdbcTemplate jdbcTemplate) {
            List<Map<String, Object>> listOfKeyValuePairs = jdbcTemplate.queryForList(
                    "select * from INFORMATION_SCHEMA.SYSTEM_SESSIONINFO");

            Map<String, String> results = listOfKeyValuePairs.stream()
                    .collect(Collectors.toMap(i -> (String)i.get("KEY"), i -> (String)i.get("VALUE")));

            return results.get("DATABASE");
        }
    }
}
