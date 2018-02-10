package me.loki2302;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = { App.class, RestTemplateConfiguration.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NoteTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void canCreateNote() throws JsonProcessingException {
        String personUrl = createPersonAndGetUrl(person("loki2302"));

        // WTF? how do I submit a Resource<Note>?
        Map<String, String> note = new HashMap<String, String>();
        note.put("text", "hello there");
        note.put("person", personUrl);

        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "http://localhost:8080/api/notes",
                note,
                Object.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getHeaders().getLocation());
    }

    private String createPersonAndGetUrl(Person person) {
        ResponseEntity<Object> responseEntity = createPerson(person);
        return responseEntity.getHeaders().getLocation().toString();
    }

    private ResponseEntity<Object> createPerson(Person person) {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "http://localhost:8080/api/people",
                person,
                Object.class);

        return responseEntity;
    }

    private static Person person(String name) {
        Person person = new Person();
        person.name = name;
        return person;
    }
}
