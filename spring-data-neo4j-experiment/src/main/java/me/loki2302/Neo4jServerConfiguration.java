package me.loki2302;

import org.apache.commons.io.FileUtils;
import org.neo4j.server.CommunityBootstrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;

@Profile("community")
@Configuration
public class Neo4jServerConfiguration {
    @Bean(destroyMethod = "stop")
    public CommunityBootstrapper communityBootstrapper() throws IOException, InterruptedException {
        // Neo4j browser: http://localhost:7474/browser/

        FileUtils.deleteDirectory(new File("./neo4j-home"));
        FileUtils.deleteDirectory(new File("./data"));

        CommunityBootstrapper communityBootstrapper = new CommunityBootstrapper();
        CommunityBootstrapper.start(communityBootstrapper, new String[] {});
        return communityBootstrapper;
    }
}
