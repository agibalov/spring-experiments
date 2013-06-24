package me.loki2302.cypher;

import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Person {
    public static final String NAME_INDEX = "name-index";
    
    @GraphId
    private Long id;
    
    @Indexed(indexName = NAME_INDEX)
    private String name;
    
    @RelatedTo(type = "LIKES", direction = Direction.OUTGOING)
    private Set<Person> likedPersons;
        
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void likePerson(Person likedPerson) {
    	likedPersons.add(likedPerson);
    }
    
    public void unlikePerson(Person likedPerson) {
    	likedPersons.remove(likedPerson);
    }
    
    @Override
    public String toString() {
    	return String.format("Person{id=%d,name=%s,likes=%d}", id, name, likedPersons.size());
    }
    
    @Override
	public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
	}
    
    @Override
    public boolean equals(Object obj) {
    	if(id == null) {
    		return false;
    	}
    	
    	if(obj == null) {
    		return false;
    	}    	
    	
    	if(!(obj instanceof Person)) {
    		return false;
    	}
    	
    	Person otherPerson = (Person)obj;
    	if(otherPerson.id == null) {
    		return false;
    	}
    	
    	return otherPerson.id == id;
    }
}