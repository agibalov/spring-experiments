package me.loki2302.transactions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
public class TransactionsTest {
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private PersonRepository personRepository;

    @Before
    public void cleanUp() {
        personRepository.deleteAll();
    }

    @Test
    public void changesAreNotCommittedIfThereIsException() {
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Person person = new Person();
                    person.name = "loki2302";
                    personRepository.save(person);

                    throw new DoNotWantException();
                }
            });

            fail();
        } catch (DoNotWantException e) {
            assertEquals(0, personRepository.findAll().size());
        }
    }

    @Test
    public void changesAreCommittedIfThereIsNoException() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Person person = new Person();
                person.name = "loki2302";
                personRepository.save(person);
            }
        });

        assertEquals(1, personRepository.findAll().size());
    }

    private static class DoNotWantException extends RuntimeException {
    }
}
