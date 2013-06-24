package me.loki2302.plain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.node.Neo4jHelper;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class AppTest {
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Rollback(false)
    @BeforeTransaction
    public void cleanUpDatabase() {
        Neo4jHelper.cleanDb(neo4jTemplate);
    }
    
    @Test
    public void thereAreNoPeopleByDefault() {
        assertNobodyThere();
    }
    
    @Test
    public void canSaveSinglePerson() {
        assertNobodyThere();
        
        Person person = new Person();
        person.setName("loki2302");
        person = personRepository.save(person);
        
        assertPeopleThere(1);
        
        assertNotNull(person.getId());
        assertEquals("loki2302", person.getName());
    }    
    
    @Test
    public void canSaveAndRetrieveSinglePerson() {
        assertNobodyThere();
        
        Person person = new Person();
        person.setName("loki2302");
        person = personRepository.save(person);
        
        assertPeopleThere(1);
        
        Long personId = person.getId();
        
        Person person1 = personRepository.findOne(personId);
        assertEquals(personId, person1.getId());
        assertEquals("loki2302", person1.getName());
    }
    
    private void assertNobodyThere() {
        long peopleCount = personRepository.count();
        assertEquals(0, peopleCount);
    }
    
    private void assertPeopleThere(long expectedPeopleCount) {
        long peopleCount = personRepository.count();
        assertEquals(expectedPeopleCount, peopleCount);
    }
}
