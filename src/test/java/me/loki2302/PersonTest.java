package me.loki2302;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.Config.class)
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersonTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NoteRepository noteRepository;

    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(mappingJackson2HttpMessageConverter));
    }

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
        assertTrue(personResource.getLink("self").getHref().startsWith("http://localhost:8080/people/"));

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
                "http://localhost:8080/people",
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
                "http://localhost:8080/people/?size=4&page=1",
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
            getPerson("http://localhost:8080/people/123");
            fail();
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    public void cantUpdatePersonThatDoesNotExist() {
        try {
            updatePerson("http://localhost:8080/people/123", person("Andrey"));
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    public void cantDeletePersonThatDoesNotExist() {
        try {
            restTemplate.delete("http://localhost:8080/people/123");
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    private ResponseEntity<Object> createPerson(Person person) {
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "http://localhost:8080/people",
                person,
                Object.class);

        return responseEntity;
    }

    private ResponseEntity<Object> updatePerson(String personUri, Person person) {
        return restTemplate.exchange(
                personUri,
                HttpMethod.PUT,
                new HttpEntity<Person>(person),
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
