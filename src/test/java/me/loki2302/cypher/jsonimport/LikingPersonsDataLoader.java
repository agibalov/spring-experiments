package me.loki2302.cypher.jsonimport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.loki2302.cypher.PersonService;

public class LikingPersonsDataLoader {	
	public void loadFromResource(PersonService personService, String jsonResourceName) {
		ObjectMapper objectMapper = new ObjectMapper();
		LikingPersonsData likingPersonsData = null;
		try {
			likingPersonsData = objectMapper.readValue(
					LikingPersonsDataLoader.class.getResource(jsonResourceName), 
					LikingPersonsData.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if(likingPersonsData == null) {
				throw new RuntimeException();
			}
		}
		
		Map<Integer, Long> personsMap = new HashMap<Integer, Long>();
		for(PersonData personData : likingPersonsData.persons) {			
			long personId = personService.createPerson(personData.name);
			personsMap.put(personData.id, personId);
		}
		
		for(LikeData likeData : likingPersonsData.likes) {
			long fromPersonId = personsMap.get(likeData.from);
			long toPersonId = personsMap.get(likeData.to);
			personService.likePerson(fromPersonId, toPersonId);
		}
	}
}
