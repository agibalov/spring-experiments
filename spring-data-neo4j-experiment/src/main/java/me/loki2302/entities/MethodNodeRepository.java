package me.loki2302.entities;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MethodNodeRepository extends GraphRepository<MethodNode> {
    MethodNode findByName(String name);

    @Query("match (class:Class {name: {className}})-[r:HAS_METHOD]->(method:Method) return method")
    List<MethodNode> findByClass(@Param("className") String className);
}
