package me.loki2302.decltransactions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DeclarativeTransactionsTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Test
    public void changesAreNotCommittedWhenTransactionalMethodThrows() {
        try {
            personService.createPerson(true);
            fail();
        } catch(OopsException e) {
        }

        assertEquals(0, personRepository.count());
    }

    @Test
    public void changesAreCommittedWhenTransactionalMethodDoesNotThrow() {
        personService.createPerson(false);
        assertEquals(1, personRepository.count());
    }
}
