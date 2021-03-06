package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = { App.class, RestTemplateConfiguration.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersonTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void canCreatePerson() {
        ResponseEntity<Object> responseEntity =
                createPerson(person("loki2302"));

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getHeaders().getLocation());
        assertTrue(responseEntity.hasBody());
    }

    @Test
    public void canGetPerson() {
        ResponseEntity<Object> responseEntity =
                createPerson(person("loki2302"));

        String personUri = responseEntity.getHeaders().getLocation().toString();

        ResponseEntity<Resource<Person>> personResponseEntity =
                getPerson(personUri);
        assertEquals(HttpStatus.OK, personResponseEntity.getStatusCode());

        Resource<Person> personResource = personResponseEntity.getBody();
        assertEquals(3, personResource.getLinks().size());
        assertTrue(personResource.getLink("self").getHref().startsWith("http://localhost:8080/api/people/"));

        Person person = personResource.getContent();
        assertEquals("loki2302", person.name);
    }

    @Test
    public void canUpdatePerson() {
        ResponseEntity<Object> responseEntity =
                createPerson(person("loki2302"));

        String personUri = responseEntity.getHeaders().getLocation().toString();

        ResponseEntity<Object> updateResponseEntity =
                updatePerson(personUri, person("Andrey"));
        assertEquals(HttpStatus.OK, updateResponseEntity.getStatusCode());

        ResponseEntity<Resource<Person>> personResponseEntity =
                getPerson(personUri);
        assertEquals("Andrey", personResponseEntity.getBody().getContent().name);
    }

    @Test
    public void canDeletePerson() {
        ResponseEntity<Object> responseEntity =
                createPerson(person("loki2302"));

        String personUri = responseEntity.getHeaders().getLocation().toString();

        restTemplate.delete(personUri);

        try {
            getPerson(personUri);
            fail();
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    public void thereAreNoPeopleByDefault() {
        ResponseEntity<PagedResources<Person>> peopleResponseEntity = restTemplate.exchange(
                "http://localhost:8080/api/people",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResources<Person>>() {});

        assertEquals(HttpStatus.OK, peopleResponseEntity.getStatusCode());
        assertTrue(peopleResponseEntity.getBody().getContent().isEmpty());
    }

    @Test
    public void canGetPersonList() {
        for(int i = 0; i < 10; ++i) {
            createPerson(person(String.format("loki2302_%d", i)));
        }

        ResponseEntity<PagedResources<Person>> peopleResponseEntity = restTemplate.exchange(
                "http://localhost:8080/api/people/?size=4&page=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResources<Person>>() {});
        assertEquals(3, peopleResponseEntity.getBody().getMetadata().getTotalPages());
        assertEquals(1, peopleResponseEntity.getBody().getMetadata().getNumber());
        assertEquals(4, peopleResponseEntity.getBody().getMetadata().getSize());
        assertEquals(10, peopleResponseEntity.getBody().getMetadata().getTotalElements());
    }

    @Test
    public void cantCreatePersonIfThereAreValidationErrors() {
        try {
            createPerson(person(""));
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    public void cantGetPersonThatDoesNotExist() {
        try {
            getPerson("http://localhost:8080/api/people/123");
            fail();
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    public void cantUpdatePersonThatDoesNotExist() {
        try {
            updatePerson("http://localhost:8080/api/people/123", person("Andrey"));
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    public void cantDeletePersonThatDoesNotExist() {
        try {
            restTemplate.delete("http://localhost:8080/api/people/123");
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    private ResponseEntity<Object> createPerson(Person person) {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "http://localhost:8080/api/people",
                person,
                Object.class);

        return responseEntity;
    }

    private ResponseEntity<Object> updatePerson(String personUri, Person person) {
        return restTemplate.exchange(
                personUri,
                HttpMethod.PUT,
                new HttpEntity<>(person),
                Object.class);
    }

    private ResponseEntity<Resource<Person>> getPerson(String personUri) {
        ResponseEntity<Resource<Person>> personResourceResponseEntity = restTemplate.exchange(
                personUri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Resource<Person>>() {});

        return personResourceResponseEntity;
    }

    private static Person person(String name) {
        Person person = new Person();
        person.name = name;
        return person;
    }
}
