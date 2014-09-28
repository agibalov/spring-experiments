package me.loki2302;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
                MappingJackson2HttpMessageConverter c = (MappingJackson2HttpMessageConverter)converter;
                c.getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            }
        }
    }

    @Test
    public void canCreateAndGet() {
        Person person = new Person();
        person.name = "loki2302";

        HttpEntity<Object> entity = restTemplate.postForEntity(
                "http://localhost:8080/people", person, Object.class);
        String personUri = entity.getHeaders().getLocation().toString();

        Person retrievedPerson = restTemplate.getForObject(personUri, Person.class);
        assertEquals("loki2302", retrievedPerson.name);

        Person person2 = new Person();
        person2.name = "Andrey";
        restTemplate.put(personUri, person2);

        retrievedPerson = restTemplate.getForObject(personUri, Person.class);
        assertEquals("Andrey", retrievedPerson.name);

        restTemplate.delete(personUri);

        try {
            restTemplate.getForObject(personUri, Person.class);
            fail();
        } catch(HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }
}
