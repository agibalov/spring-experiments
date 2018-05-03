package io.agibalov.transactions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Exchanger;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IsolationTest {
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private PersonRepository personRepository;

    @Before
    public void cleanUp() {
        personRepository.deleteAll();
    }

    // I am not quite sure this test is reliable.
    @Test
    public void canGetUpdateConflictWhenUsingSerializableTransactionIsolationLevel() throws InterruptedException {
        Person person = new Person();
        person.name = "loki2302";
        person = personRepository.save(person);

        long personId = person.id;

        Exchanger<Void> thread1BeforeReadExchanger = new Exchanger<>();
        Exchanger<Void> thread1AfterReadExchanger = new Exchanger<>();
        Exchanger<Void> thread1BeforeSaveExchanger = new Exchanger<>();
        Exchanger<Throwable> thread1TransactionExceptionExchanger = new Exchanger<>();
        new Thread(() -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);

            Throwable transactionException = null;
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        try {
                            thread1BeforeReadExchanger.exchange(null);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        Person person = personRepository.findOne(personId);

                        try {
                            thread1AfterReadExchanger.exchange(null);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        person.name = "a";

                        try {
                            thread1BeforeSaveExchanger.exchange(null);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        personRepository.save(person);
                    }
                });
            } catch (RuntimeException e) {
                transactionException = e;
            } finally {
                try {
                    thread1TransactionExceptionExchanger.exchange(transactionException);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Exchanger<Void> thread2BeforeReadExchanger = new Exchanger<>();
        Exchanger<Void> thread2AfterReadExchanger = new Exchanger<>();
        Exchanger<Void> thread2BeforeSaveExchanger = new Exchanger<>();
        Exchanger<Throwable> thread2TransactionExceptionExchanger = new Exchanger<>();
        new Thread(() -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);

            Throwable transactionException = null;
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        try {
                            thread2BeforeReadExchanger.exchange(null);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        Person person = personRepository.findOne(personId);

                        try {
                            thread2AfterReadExchanger.exchange(null);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        person.name = "b";

                        try {
                            thread2BeforeSaveExchanger.exchange(null);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        personRepository.save(person);
                    }
                });
            } catch (RuntimeException e) {
                transactionException = e;
            } finally {
                try {
                    thread2TransactionExceptionExchanger.exchange(transactionException);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // MAKE SURE THREAD1 READS THE DATA BEFORE THREAD2 DOES IT

        // make sure thread1 has read the data
        thread1BeforeReadExchanger.exchange(null); // sync start
        thread1AfterReadExchanger.exchange(null); // sync stop

        // make sure thread2 has read the data
        thread2BeforeReadExchanger.exchange(null); // sync start
        thread2AfterReadExchanger.exchange(null); // sync stop

        // MAKE SURE THREAD1 UPDATES THE DATA AND COMMITS THE CHANGES BEFORE THREAD2 DOES IT

        // make sure thread1 has updated the data and committed the changes
        thread1BeforeSaveExchanger.exchange(null); // sync start update
        Throwable thread1TransactionException = thread1TransactionExceptionExchanger.exchange(null); // sync stop transaction
        assertNull(thread1TransactionException); // committed successfully

        // make sure thread2 tried to update the data, but failed
        thread2BeforeSaveExchanger.exchange(null); // sync start update
        Throwable thread2TransactionException = thread2TransactionExceptionExchanger.exchange(null); // sync stop transaction
        assertNotNull(thread2TransactionException); // commit failed
        assertTrue(thread2TransactionException instanceof CannotAcquireLockException); // failed to acquire lock ("serialization failed")


        // make sure that eventually there's a change that thread1 made
        person = personRepository.findOne(personId);
        assertEquals("a", person.name);
    }
}
