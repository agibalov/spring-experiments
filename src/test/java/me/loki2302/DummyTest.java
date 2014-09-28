package me.loki2302;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.Config.class)
@WebAppConfiguration
@IntegrationTest
public class DummyTest {
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        for(HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if(converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                        (MappingJackson2HttpMessageConverter)converter;

                ObjectMapper objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();
                objectMapper.registerModules(new Jackson2HalModule());
            }
        }
    }

    @Test
    public void canCreateAndGet() {
        //
        Person person = new Person();
        person.name = "loki2302";

        ResponseEntity<Object> createdPersonResponseEntity = createPerson(person);
        String personUri = createdPersonResponseEntity
                .getHeaders().getLocation().toString();
        //

        //
        ResponseEntity<Resource<Person>> retrievedPersonResource =
                getPerson(personUri);

        assertEquals(HttpStatus.OK, retrievedPersonResource.getStatusCode());
        assertEquals("loki2302", retrievedPersonResource.getBody().getContent().name);
        assertEquals(1, retrievedPersonResource.getBody().getLinks().size());
        assertEquals("http://localhost:8080/people/1", retrievedPersonResource.getBody().getLink("self").getHref());
        //

        //
        Person person2 = new Person();
        person2.name = "Andrey";
        updatePerson(personUri, person2);
        //

        //
        retrievedPersonResource = getPerson(personUri);
        assertEquals("Andrey", retrievedPersonResource.getBody().getContent().name);
        //

        //
        restTemplate.delete(personUri);
        //

        try {
            getPerson(personUri);
            fail();
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
}
