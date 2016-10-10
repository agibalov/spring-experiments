package me.loki2302;

import me.loki2302.entities.ClassNode;
import me.loki2302.entities.ClassNodeIdAndName;
import me.loki2302.entities.ClassNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Profile("app")
@EnableNeo4jRepositories(basePackageClasses = ClassNode.class)
public class AppRunner implements CommandLineRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    private ClassNodeRepository classNodeRepository;

    @Autowired
    private CodeReader codeReader;

    @Override
    public void run(String... args) throws Exception {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));

        List<ClassNodeIdAndName> idsAndNames = classNodeRepository.getAllIdsAndNames();
        for(ClassNodeIdAndName classNodeIdAndName : idsAndNames) {
            LOGGER.info("id={}, name={}", classNodeIdAndName.id, classNodeIdAndName.name);
        }
    }
}
