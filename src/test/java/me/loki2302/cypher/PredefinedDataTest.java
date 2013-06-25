package me.loki2302.cypher;

import static org.junit.Assert.*;

import me.loki2302.cypher.jsonimport.LikingPersonsDataLoader;

import org.junit.Before;
import org.junit.Test;

public class PredefinedDataTest extends AbstractPersonServiceTest {
	@Before
	public void loadTestData() {
		LikingPersonsDataLoader loader = new LikingPersonsDataLoader();
    	loader.loadFromResource(personService, "/persons.json");
	}
		
	@Test
	public void canLoadDataCorrectly() {
		long personCount = personService.getPersonCount();
		assertEquals(8, personCount);
		
		long loki2302 = personService.getPersonByName("loki2302").id;
		long millasa = personService.getPersonByName("millasa").id;
		long mhunger = personService.getPersonByName("mhunger").id;
		long kreeves = personService.getPersonByName("kreeves").id;
		long gandalf = personService.getPersonByName("gandalf").id;
		long neo4j = personService.getPersonByName("neo4j").id;
		long sjobs = personService.getPersonByName("sjobs").id;
		long iphone = personService.getPersonByName("iphone").id;
		
		assertPersonLikesPersons(loki2302, millasa, mhunger, kreeves, neo4j);
		assertPersonLikesPersons(millasa, loki2302, kreeves, gandalf);
		assertPersonLikesPersons(mhunger, neo4j);
		assertPersonLikesPersons(kreeves);
		assertPersonLikesPersons(gandalf);
		assertPersonLikesPersons(neo4j);
		assertPersonLikesPersons(sjobs, iphone);		
		assertPersonLikesPersons(iphone);
	}
}
