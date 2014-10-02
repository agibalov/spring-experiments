package me.loki2302.repositoryextension;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
public class RepositoryExtensionTest {
    @Autowired
    private PersonRepository personRepository;

    @Test
    public void canUseExtendedRepository() {
        Person person = personRepository.customFindOne(1);
        assertNull(person);

        person = new Person();
        person.name = "loki2302";
        personRepository.save(person);

        person = personRepository.customFindOne(1);
        assertNotNull(person);
        assertEquals("loki2302", person.name);
    }
}
