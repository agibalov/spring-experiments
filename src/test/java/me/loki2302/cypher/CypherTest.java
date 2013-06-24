package me.loki2302.cypher;

import static org.junit.Assert.*;

import java.util.List;

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

// TODO: define more or less complicated graph and make sure I can retrieve its parts (likes/likedby)
// TODO: get-not-likes
// TODO: get-not-liked-by
// TODO: get most/least liked
// TODO: get N most/least liked
// TODO: get most/least liking
// TODO: get N most/least liking

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class CypherTest {
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Autowired
    private PersonService personService;
    
    @Rollback(false)
    @BeforeTransaction
    public void cleanUpDatabase() {
        Neo4jHelper.cleanDb(neo4jTemplate);
    }
    
    @Test
    public void thereAreNoPersonsByDefault() {
    	long personCount = personService.getPersonCount();
    	assertEquals(0, personCount);
    }
    
    @Test
    public void canCreatePerson() {
    	long personId = personService.createPerson("loki2302");    	
    	
    	long personCount = personService.getPersonCount();
    	assertEquals(1, personCount);
    	
    	PersonDTO personById = personService.getPersonById(personId);
    	assertEquals(personId, personById.id);
    	assertEquals("loki2302", personById.name);
    	
    	PersonDTO personByName = personService.getPersonByName("loki2302");
    	assertEquals(personId, personByName.id);
    	assertEquals("loki2302", personByName.name); 
    }
        
    @Test(expected = RuntimeException.class)
    public void cantGetPersonByIdIfPersonDoesNotExist() {
    	long personCount = personService.getPersonCount();
    	assertEquals(0, personCount);
    	personService.getPersonById(123);
    }
        
    @Test(expected = RuntimeException.class)
    public void cantGetPersonByNameIfPersonDoesNotExist() {
    	long personCount = personService.getPersonCount();
    	assertEquals(0, personCount);
    	personService.getPersonByName("loki2302");
    }
    
    @Test
    public void canDeletePerson() {
    	long personId = personService.createPerson("loki2302");
    	
    	long personCount = personService.getPersonCount();
    	assertEquals(1, personCount);
    	
    	personService.deletePerson(personId);
    	
    	personCount = personService.getPersonCount();
    	assertEquals(0, personCount);
    }
    
    @Test(expected = RuntimeException.class)
    public void cantDeletePersonThatDoesNotExist() {
    	long personCount = personService.getPersonCount();
    	assertEquals(0, personCount);
    	personService.deletePerson(123);
    }
    
    @Test
    public void canChangePersonName() {
    	long personId = personService.createPerson("loki2302");
    	personService.setPersonName(personId, "qwerty");
    	PersonDTO updatedPerson = personService.getPersonById(personId);
    	assertEquals(personId, updatedPerson.id);
    	assertEquals("qwerty", updatedPerson.name);
    }
    
    @Test(expected = RuntimeException.class)
    public void cantChangePersonNameIfPersonDoesNotExist() {
    	long personCount = personService.getPersonCount();
    	assertEquals(0, personCount);
    	personService.setPersonName(123, "qwerty");
    }
    
    @Test
    public void canLikePerson() {
    	long likingPersonId = personService.createPerson("loki2302");
    	long personIdToLike = personService.createPerson("qwerty");
    	
    	assertPersonLikesNobody(likingPersonId);
    	assertPersonLikedByNobody(likingPersonId);
    	assertPersonLikesNobody(personIdToLike);
    	assertPersonLikedByNobody(personIdToLike);
    	
    	personService.likePerson(likingPersonId, personIdToLike);    	
    	
    	assertPersonLikesOnePerson(likingPersonId, personIdToLike);
    	assertPersonLikedByNobody(likingPersonId);
    	assertPersonLikesNobody(personIdToLike);
    	assertPersonLikedByOnePerson(personIdToLike, likingPersonId);
    	
    	personService.unlikePerson(likingPersonId, personIdToLike);
    	
    	assertPersonLikesNobody(likingPersonId);
    	assertPersonLikedByNobody(likingPersonId);
    	assertPersonLikesNobody(personIdToLike);
    	assertPersonLikedByNobody(personIdToLike);
    }
    
    @Test(expected = RuntimeException.class)
    public void cantLikePersonWhenLikingPersonDoesNotExist() {    	
    	long personIdToLike = personService.createPerson("qwerty");
    	personService.likePerson(123, personIdToLike);
    }
    
    @Test(expected = RuntimeException.class)
    public void cantLikePersonWhenLikedPersonDoesNotExist() {    	
    	long likingPersonId = personService.createPerson("qwerty");
    	personService.likePerson(likingPersonId, 123);
    }
    
    private static void assertSamePerson(PersonDTO person1, PersonDTO person2) {
    	assertEquals(person1.id, person2.id);
    	assertEquals(person1.name, person2.name);
    }
    
    private void assertPersonLikesNobody(long likingPersonId) {
    	List<PersonDTO> likedPersons = personService.getLikedPersons(likingPersonId);
    	assertEquals(0, likedPersons.size());
    }
    
    private void assertPersonLikesOnePerson(long likingPersonId, long likedPersonId) {
    	List<PersonDTO> likedPersons = personService.getLikedPersons(likingPersonId);
    	assertEquals(1, likedPersons.size());    	
    	PersonDTO theOnlyLikedPerson = likedPersons.get(0);
    	PersonDTO personToLike = personService.getPersonById(likedPersonId);
    	assertSamePerson(personToLike, theOnlyLikedPerson);
    }
    
    private void assertPersonLikedByNobody(long likedPersonId) {
    	List<PersonDTO> likingPersons = personService.getLikingPersons(likedPersonId);
    	assertEquals(0, likingPersons.size());
    }
    
    private void assertPersonLikedByOnePerson(long likedPersonId, long likingPersonId) {
    	List<PersonDTO> likingPersons = personService.getLikingPersons(likedPersonId);
    	assertEquals(1, likingPersons.size());    	
    	PersonDTO theOnlyLikingPerson = likingPersons.get(0);
    	PersonDTO personLikedBy = personService.getPersonById(likingPersonId);
    	assertSamePerson(personLikedBy, theOnlyLikingPerson);
    } 
}
