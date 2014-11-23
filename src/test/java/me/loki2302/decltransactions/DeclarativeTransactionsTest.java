package me.loki2302.decltransactions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
public class DeclarativeTransactionsTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Test
    public void canUseDeclarativeTransactions() {
        try {
            personService.createPersonAndThenThrow();
            fail();
        } catch(OopsException e) {
        }

        assertEquals(0, personRepository.count());
    }
}
