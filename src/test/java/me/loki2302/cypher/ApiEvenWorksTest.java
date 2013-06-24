package me.loki2302.cypher;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApiEvenWorksTest extends AbstractPersonServiceTest {       
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
    	assertPersonHasOnePersonToLike(likingPersonId, personIdToLike);
    	assertPersonHasOnePersonToBeLikedBy(likingPersonId, personIdToLike);
    	
    	assertPersonLikesNobody(personIdToLike);
    	assertPersonLikedByNobody(personIdToLike);    	
    	assertPersonHasOnePersonToLike(personIdToLike, likingPersonId);
    	assertPersonHasOnePersonToBeLikedBy(personIdToLike, likingPersonId);
    	
    	personService.likePerson(likingPersonId, personIdToLike);    	
    	
    	assertPersonLikesOnePerson(likingPersonId, personIdToLike);
    	assertPersonLikedByNobody(likingPersonId);
    	assertPersonHasNoPersonsToLike(likingPersonId);
    	assertPersonHasOnePersonToBeLikedBy(likingPersonId, personIdToLike);
    	
    	assertPersonLikesNobody(personIdToLike);
    	assertPersonLikedByOnePerson(personIdToLike, likingPersonId);
    	assertPersonHasOnePersonToLike(personIdToLike, likingPersonId);
    	assertPersonHasNoPersonsToBeLikedBy(personIdToLike);
    	
    	personService.unlikePerson(likingPersonId, personIdToLike);
    	
    	assertPersonLikesNobody(likingPersonId);
    	assertPersonLikedByNobody(likingPersonId);
    	assertPersonHasOnePersonToLike(likingPersonId, personIdToLike);
    	assertPersonHasOnePersonToBeLikedBy(likingPersonId, personIdToLike);
    	
    	assertPersonLikesNobody(personIdToLike);
    	assertPersonLikedByNobody(personIdToLike);
    	assertPersonHasOnePersonToLike(personIdToLike, likingPersonId);
    	assertPersonHasOnePersonToBeLikedBy(personIdToLike, likingPersonId);
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
}
