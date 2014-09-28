package me.loki2302;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.Config.class)
@WebAppConfiguration
@IntegrationTest
public class DummyTest {
    @Test
    public void canCreateAndGet() {
        Person person = new Person();
        person.name = "loki2302";

        RestTemplate restTemplate = new RestTemplate();
        for(HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if(converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter c = (MappingJackson2HttpMessageConverter)converter;
                c.getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            }
        }


        HttpEntity<Object> entity = restTemplate.postForEntity(
                "http://localhost:8080/people", person, Object.class);
        String location = entity.getHeaders().getLocation().toString();

        Person retrievedPerson = restTemplate.getForObject(location, Person.class);
        assertEquals("loki2302", retrievedPerson.name);
    }
}
