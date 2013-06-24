package me.loki2302.cypher;

import static org.junit.Assert.*;

import java.util.List;

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
public abstract class AbstractPersonServiceTest {
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Autowired
    protected PersonService personService;
    
    @Rollback(false)
    @BeforeTransaction
    public void cleanUpDatabase() {
        Neo4jHelper.cleanDb(neo4jTemplate);
    }
    
    protected static void assertSamePerson(PersonDTO person1, PersonDTO person2) {
    	assertEquals(person1.id, person2.id);
    	assertEquals(person1.name, person2.name);
    }
    
    protected void assertPersonLikesNobody(long likingPersonId) {
    	List<PersonDTO> likedPersons = personService.getLikedPersons(likingPersonId);
    	assertEquals(0, likedPersons.size());
    }
    
    protected void assertPersonHasNoPersonsToLike(long likingPersonId) {    	
    	List<PersonDTO> likingPersons = personService.getPersonsToLike(likingPersonId);
    	assertEquals(0, likingPersons.size());
    }    
    
    protected void assertPersonHasOnePersonToLike(long likingPersonId, long personIdToLike) {
    	List<PersonDTO> personsToLike = personService.getPersonsToLike(likingPersonId);
    	assertEquals(1, personsToLike.size());    	
    	PersonDTO theOnlyPersonToLike = personsToLike.get(0);
    	PersonDTO personToLike = personService.getPersonById(personIdToLike);
    	assertSamePerson(personToLike, theOnlyPersonToLike);
    }
    
    protected void assertPersonLikesOnePerson(long likingPersonId, long likedPersonId) {
    	List<PersonDTO> likedPersons = personService.getLikedPersons(likingPersonId);
    	assertEquals(1, likedPersons.size());    	
    	PersonDTO theOnlyLikedPerson = likedPersons.get(0);
    	PersonDTO personToLike = personService.getPersonById(likedPersonId);
    	assertSamePerson(personToLike, theOnlyLikedPerson);
    }
    
    protected void assertPersonLikedByNobody(long likedPersonId) {
    	List<PersonDTO> likingPersons = personService.getLikingPersons(likedPersonId);
    	assertEquals(0, likingPersons.size());
    }
    
    protected void assertPersonLikedByOnePerson(long likedPersonId, long likingPersonId) {
    	List<PersonDTO> likingPersons = personService.getLikingPersons(likedPersonId);
    	assertEquals(1, likingPersons.size());    	
    	PersonDTO theOnlyLikingPerson = likingPersons.get(0);
    	PersonDTO personLikedBy = personService.getPersonById(likingPersonId);
    	assertSamePerson(personLikedBy, theOnlyLikingPerson);
    } 
    
    protected void assertPersonHasNoPersonsToBeLikedBy(long likedPersonId) {
    	List<PersonDTO> personsToBeLikedBy = personService.getPersonsToBeLikedBy(likedPersonId);
    	assertEquals(0, personsToBeLikedBy.size());
    }
    
    protected void assertPersonHasOnePersonToBeLikedBy(long likedPersonId, long likingPersonId) {
    	List<PersonDTO> personsToBeLikedBy = personService.getPersonsToBeLikedBy(likedPersonId);
    	assertEquals(1, personsToBeLikedBy.size());
    	PersonDTO theOnlyPersonToBeLikedBy = personsToBeLikedBy.get(0);
    	PersonDTO likingPerson = personService.getPersonById(likingPersonId);
    	assertSamePerson(likingPerson, theOnlyPersonToBeLikedBy);
    }
}
