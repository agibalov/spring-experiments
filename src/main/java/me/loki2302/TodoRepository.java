package me.loki2302;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface TodoRepository extends GraphRepository<Todo> {
    Todo findByText(String text);
}
