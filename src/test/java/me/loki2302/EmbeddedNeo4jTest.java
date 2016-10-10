package me.loki2302;

import me.loki2302.dummy.Adder;
import me.loki2302.dummy.Calculator;
import me.loki2302.dummy.Subtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("embedded,test")
@SpringBootTest(properties = {
        "spring.data.neo4j.driver=org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
public class EmbeddedNeo4jTest {
    @Autowired
    private CodeReader codeReader;

    @Autowired
    private ClassNodeRepository classNodeRepository;

    @Test
    public void dummy() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));
        List<ClassNodeIdAndName> classNodeIdsAndNames = classNodeRepository.getAllIdsAndNames();
        assertEquals(3, classNodeIdsAndNames.size());
        assertTrue((classNodeIdsAndNames.stream().anyMatch(c -> c.name.equals(Adder.class.getSimpleName()))));
        assertTrue((classNodeIdsAndNames.stream().anyMatch(c -> c.name.equals(Subtractor.class.getSimpleName()))));
        assertTrue((classNodeIdsAndNames.stream().anyMatch(c -> c.name.equals(Calculator.class.getSimpleName()))));
    }
}
