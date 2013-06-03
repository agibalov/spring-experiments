package me.loki2302.cypher;

import org.springframework.data.neo4j.annotation.MapResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.ResultColumn;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface PeopleRepository extends GraphRepository<Person> {
    @Query( "start person=node:`name-index`('name:*') " + 
            "return person.name as name, person.age as age " + 
            "order by name asc")
    Iterable<NameAndAgeResult> getNamesAndAges();
        
    @MapResult
    public static interface NameAndAgeResult {
        @ResultColumn("name")
        String getName();
        
        @ResultColumn("age")
        int getAge();
    }
}