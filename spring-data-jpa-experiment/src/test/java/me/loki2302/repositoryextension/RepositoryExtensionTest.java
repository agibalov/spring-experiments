package me.loki2302.repositoryextension;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
