package me.loki2302.app;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

@Configuration
@EnableNeo4jRepositories(basePackageClasses = App.class)
public class AppNeo4jConfiguration extends Neo4jConfiguration {
    @Bean(name = "graphDatabaseService")
    public GraphDatabaseService provideGraphDatabaseService() {
        return new EmbeddedGraphDatabase("target/mydb1");
    }
}