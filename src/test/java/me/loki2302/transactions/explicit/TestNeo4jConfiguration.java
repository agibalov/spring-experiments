package me.loki2302.transactions.explicit;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

@Configuration
@EnableNeo4jRepositories(basePackageClasses = TestNeo4jConfiguration.class)
public class TestNeo4jConfiguration extends Neo4jConfiguration {
    public TestNeo4jConfiguration() {
        setBasePackage("me.loki2302.transactions.explicit");
    }

    @Bean(name = "graphDatabaseService")
    public GraphDatabaseService provideGraphDatabaseService() {
        return new GraphDatabaseFactory().newEmbeddedDatabase("target/mydb4");
    }
}