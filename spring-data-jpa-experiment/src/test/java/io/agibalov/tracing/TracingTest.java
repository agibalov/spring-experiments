package io.agibalov.tracing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext
public class TracingTest {
    @Autowired
    private SqlTracer sqlTracer;

    @Autowired
    private BlogRepository blogRepository;

    @Test
    public void itShouldInterceptAllQueries() {
        sqlTracer.clear();

        List<String> queries = sqlTracer.getQueries();

        blogRepository.save(Blog.builder()
                .id("blog1")
                .title("The Blog One")
                .build());

        assertEquals(2, queries.size());
        assertTrue(queries.get(0).matches("^select .+ from blog .+ where .+id=\\?$"));
        assertTrue(queries.get(1).matches("^insert into blog \\(title, id\\) values \\(\\?, \\?\\)$"));


        blogRepository.delete("blog1");

        assertEquals(4, queries.size());
        assertTrue(queries.get(0).matches("^select .+ from blog .+ where .+id=\\?$"));
        assertTrue(queries.get(1).matches("^insert into blog \\(title, id\\) values \\(\\?, \\?\\)$"));
        assertTrue(queries.get(2).matches("^select .+ from blog .+ where .+id=\\?$"));
        assertTrue(queries.get(3).matches("^delete from blog where id=\\?$"));
    }
}
