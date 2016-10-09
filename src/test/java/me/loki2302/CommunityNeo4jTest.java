package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("community")
@SpringBootTest(properties = {
        "spring.data.neo4j.driver=org.neo4j.ogm.drivers.http.driver.HttpDriver",
        "spring.data.neo4j.URI=http://localhost:7474"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
public class CommunityNeo4jTest {
    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void dummy() {
        if(true) {
            Todo todo1 = new Todo();
            todo1.text = "todo1";
            todo1 = todoRepository.save(todo1);

            Todo todo2 = new Todo();
            todo2.text = "todo2";
            todo2 = todoRepository.save(todo2);

            Todo todo3 = new Todo();
            todo3.text = "todo3";
            todo3 = todoRepository.save(todo3);

            todo1.relatedTodos.add(todo2);
            todo1.relatedTodos.add(todo3);

            todo1 = todoRepository.save(todo1);
        }

        if(true) {
            Todo todo2 = todoRepository.findByText("todo2");
            assertEquals("todo2", todo2.text);
            assertEquals(1, todo2.relatedTodos.size());
        }
    }
}
