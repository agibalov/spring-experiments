package me.loki2302;


import org.springframework.data.neo4j.repository.GraphRepository;

public interface PeopleRepository extends GraphRepository<Person> {        
}