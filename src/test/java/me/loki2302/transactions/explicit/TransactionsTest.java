package me.loki2302.transactions.explicit;

import static org.junit.Assert.*;

import me.loki2302.plain.TestConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class TransactionsTest {
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Test
    public void testFailure() {
        long countBefore = neo4jTemplate.count(Person.class);
        
        Person person = new Person();
        Transaction tx = neo4jTemplate.getGraphDatabase().beginTx();
        try {
            neo4jTemplate.save(person);
            tx.failure();            
        } finally {
            tx.finish();
        }
        
        long countAfter = neo4jTemplate.count(Person.class);
        assertEquals(countBefore, countAfter);
    }
    
    @Test
    public void testSuccess() {
        long countBefore = neo4jTemplate.count(Person.class);
        
        Person person = new Person();
        Transaction tx = neo4jTemplate.getGraphDatabase().beginTx();
        try {
            neo4jTemplate.save(person);
            tx.success();            
        } finally {
            tx.finish();
        }
        
        long countAfter = neo4jTemplate.count(Person.class);
        assertEquals(countBefore + 1, countAfter);
    }
}
