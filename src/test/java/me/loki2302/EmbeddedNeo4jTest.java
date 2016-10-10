package me.loki2302;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("embedded,test")
@SpringBootTest(properties = {
        "spring.data.neo4j.driver=org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
public class EmbeddedNeo4jTest extends AbstractNeo4jTest {
}
