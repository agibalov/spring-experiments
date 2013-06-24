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
	public void thereAre2PersonsByDefault() {
		long personCount = personService.getPersonCount();
		assertEquals(2, personCount);
	}    
}
