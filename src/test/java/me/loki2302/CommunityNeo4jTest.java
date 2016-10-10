package me.loki2302;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("community,test")
@SpringBootTest(properties = {
        "spring.data.neo4j.driver=org.neo4j.ogm.drivers.http.driver.HttpDriver",
        "spring.data.neo4j.URI=http://localhost:7474"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
public class CommunityNeo4jTest extends AbstractNeo4jTest {
}
