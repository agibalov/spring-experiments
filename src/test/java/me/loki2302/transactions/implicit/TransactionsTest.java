package me.loki2302.transactions.implicit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class TransactionsTest {
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Autowired
    private MyService service;
    
    @Test
    public void testFailure() {
        long countBefore = neo4jTemplate.count(Person.class);
        
        try {
            service.doFailure();
        } catch(RuntimeException e) {
            long countAfter = neo4jTemplate.count(Person.class);
            assertEquals(countBefore, countAfter);
            return;
        }
        
        fail();
    }
    
    @Test
    public void testSuccess() {
        long countBefore = neo4jTemplate.count(Person.class);
        
        service.doSuccess();
        
        long countAfter = neo4jTemplate.count(Person.class);
        assertEquals(countBefore + 1, countAfter);
    }
    
    @Service
    @Transactional
    public static class MyService {
        @Autowired
        private Neo4jTemplate neo4jTemplate;        
        
        public void doSuccess() {
            Person person = new Person();
            neo4jTemplate.save(person);
        }
        
        public void doFailure() {
            Person person = new Person();
            neo4jTemplate.save(person);
            throw new RuntimeException();
        }
    }
}
