package me.loki2302.cypher;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonService {
	@Autowired
	private PersonRepository personRepository;
	
	public long getPersonCount() {
		return personRepository.count();
	}
	
	public long createPerson(String personName) {
		Person person = new Person();
		person.setName(personName);
		person = personRepository.save(person);    		
		return person.getId();    		
	}
	
	public PersonDTO getPersonById(long personId) {
		Person person = personRepository.findOne(personId);
		if(person == null) {
			throw new RuntimeException("no such person");
		}
		
		return personDtoFromPerson(person);    		
	}
	
	public PersonDTO getPersonByName(String personName) {
		Person person = personRepository.findByPropertyValue("name", personName);
		if(person == null) {
			throw new RuntimeException("no such person");
		}
		
		return personDtoFromPerson(person);
	}
	
	public void deletePerson(long personId) {
		Person person = personRepository.findOne(personId);
		if(person == null) {
			throw new RuntimeException("no such person");
		}
		
		personRepository.delete(personId);
	}
	
	public void setPersonName(long personId, String personName) {
		Person person = personRepository.findOne(personId);
		if(person == null) {
			throw new RuntimeException("no such person");
		}
		
		person.setName(personName);
		person = personRepository.save(person);    		
	}
	
	public void likePerson(long likingPersonId, long likedPersonId) {
		Person likingPerson = personRepository.findOne(likingPersonId);
		if(likingPerson == null) {
			throw new RuntimeException("no such liking person");
		}
		
		Person likedPerson = personRepository.findOne(likedPersonId);
		if(likedPerson == null) {
			throw new RuntimeException("no such liked person");
		}
		
		likingPerson.likePerson(likedPerson);
		personRepository.save(likingPerson);
	}
	
	public void unlikePerson(long likingPersonId, long likedPersonId) {
		Person likingPerson = personRepository.findOne(likingPersonId);
		if(likingPerson == null) {
			throw new RuntimeException("no such liking person");
		}
		
		Person likedPerson = personRepository.findOne(likedPersonId);
		if(likedPerson == null) {
			throw new RuntimeException("no such liked person");
		}
		
		likingPerson.unlikePerson(likedPerson);
		personRepository.save(likingPerson);
	}
	    	
	public List<PersonDTO> getLikedPersons(long likingPersonId) {
		Person likingPerson = personRepository.findOne(likingPersonId);
		if(likingPerson == null) {
			throw new RuntimeException("no such liking person");
		}
		
		Iterable<Person> likedPersons = personRepository.getLikedPersons(likingPerson);
		return personDtosFromPersons(likedPersons);
	}
	
	public List<PersonDTO> getLikingPersons(long likedPersonId) {
		Person likedPerson = personRepository.findOne(likedPersonId);
		if(likedPerson == null) {
			throw new RuntimeException("no such liked person");
		}
		
		Iterable<Person> likingPersons = personRepository.getLikingPersons(likedPerson);
		return personDtosFromPersons(likingPersons);
	}   	
	
	public List<PersonDTO> getPersonsToLike(long likingPersonId) {
		Person likingPerson = personRepository.findOne(likingPersonId);
		if(likingPerson == null) {
			throw new RuntimeException("no such liking person");
		}
		
		Iterable<Person> likedPersons = personRepository.getPersonsToLike(likingPerson);
		return personDtosFromPersons(likedPersons);
	}
	
	private static PersonDTO personDtoFromPerson(Person person) {
		PersonDTO personDto = new PersonDTO();
		personDto.id = person.getId();
		personDto.name = person.getName();
		return personDto;
	}
	
	private static List<PersonDTO> personDtosFromPersons(Iterable<Person> persons) {
		List<PersonDTO> personDtos = new ArrayList<PersonDTO>();
		for(Person person : persons) {
			PersonDTO personDto = personDtoFromPerson(person);
			personDtos.add(personDto);
		}
		return personDtos;
	}
}