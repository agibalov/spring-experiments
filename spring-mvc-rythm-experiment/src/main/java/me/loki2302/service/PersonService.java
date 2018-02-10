package me.loki2302.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PersonService {
    private final Map<Integer, Person> people = new HashMap<Integer, Person>(); 
    
    public PersonService() {
        people.put(1, person(1, "loki", "2302"));
        people.put(2, person(2, "John", "Smith"));
        people.put(2, person(2, "Bill", "Gates"));
    }
    
    public Person getPerson(int personId) {
        return people.get(personId);
    }
    
    public Collection<Person> getPeople() {
        return people.values();
    }
    
    private static Person person(int id, String firstName, String lastName) {
        Person person = new Person();
        person.id = id;
        person.firstName = firstName;
        person.lastName = lastName;
        return person;
    }
}