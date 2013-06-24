package me.loki2302.cypher;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

public interface PersonRepository extends GraphRepository<Person>, RelationshipOperationsRepository<Person> {
	@Query("start likingPerson=node({0}) match likingPerson-[:LIKES]->likedPerson return likedPerson")
	Iterable<Person> getLikedPersons(Person likingPerson);
	
	@Query("start likedPerson=node({0}) match likingPerson-[:LIKES]->likedPerson return likingPerson")
	Iterable<Person> getLikingPersons(Person likedPerson);
	
	@Query(
			"start likingPerson=node({0}), personToLike=node:__types__(className='me.loki2302.cypher.Person') " +
			"where not (likingPerson-[:LIKES]->personToLike) and likingPerson <> personToLike " +
			"return personToLike")	
	Iterable<Person> getPersonsToLike(Person likingPerson);
}