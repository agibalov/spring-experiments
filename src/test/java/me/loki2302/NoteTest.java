package me.loki2302;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.Config.class)
@WebAppConfiguration
@IntegrationTest
public class NoteTest {
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

        /*OrderAwarePluginRegistry<RelProvider, Class<?>> relProviderPluginRegistry =
                OrderAwarePluginRegistry.create(Arrays.asList(
                        new DefaultRelProvider(),
                        new AnnotationRelProvider()));

        DelegatingRelProvider delegatingRelProvider =
                new DelegatingRelProvider(relProviderPluginRegistry);

        objectMapper.setHandlerInstantiator(
                new Jackson2HalModule.HalHandlerInstantiator(
                        delegatingRelProvider, null));*/

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(mappingJackson2HttpMessageConverter));

        noteRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    public void canCreateNote() throws JsonProcessingException {
        String personUrl = createPersonAndGetUrl(person("loki2302"));

        // WTF? how do I submit a Resource<Note>?
        Map<String, String> note = new HashMap<String, String>();
        note.put("text", "hello there");
        note.put("person", personUrl);

        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "http://localhost:8080/notes",
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
                "http://localhost:8080/people",
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
