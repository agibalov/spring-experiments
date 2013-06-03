package me.loki2302.cypher;

import static org.junit.Assert.*;

import java.util.List;

import me.loki2302.cypher.PeopleRepository.NameAndAgeResult;

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

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class CypherTest {
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Autowired
    private PeopleRepository peopleRepository;
    
    @Rollback(false)
    @BeforeTransaction
    public void cleanUpDatabase() {
        Neo4jHelper.cleanDb(neo4jTemplate);
    }
    
    @Test
    public void canGetNamesAndAges() {
        peopleRepository.save(makePerson("loki2302", 10));
        peopleRepository.save(makePerson("johnsmith", 20));
        
        List<NameAndAgeResult> nameResults = Lists.newArrayList(peopleRepository.getNamesAndAges());
        
        assertEquals("johnsmith", nameResults.get(0).getName());
        assertEquals(20, nameResults.get(0).getAge());
        
        assertEquals("loki2302", nameResults.get(1).getName());
        assertEquals(10, nameResults.get(1).getAge());
    }    
        
    private static Person makePerson(String name, int age) {
        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        return person;
    }
}
