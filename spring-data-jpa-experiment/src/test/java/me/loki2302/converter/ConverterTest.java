package me.loki2302.converter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConverterTest {
    @Autowired
    private PersonRepository personRepository;

    @Test
    public void itShouldWork() {
        {
            Person person = new Person();
            person.setId("1");
            person.setComplexDetails(SomeComplexDetails.builder()
                    .comments("qwerty")
                    .age(123)
                    .build());
            person.setInterests(new HashSet<>(Arrays.asList("cats", "bikes")));
            personRepository.save(person);
        }

        Person person = personRepository.findOne("1");
        assertEquals("1", person.getId());
        assertEquals("qwerty", person.getComplexDetails().getComments());
        assertEquals(123, person.getComplexDetails().getAge());
        assertEquals(new HashSet<>(Arrays.asList("cats", "bikes")), person.getInterests());
    }

    @SpringBootApplication
    @EntityScan
    @EnableJpaRepositories
    public static class Config {
    }
}
