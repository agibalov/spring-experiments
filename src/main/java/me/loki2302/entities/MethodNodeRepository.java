package me.loki2302.entities;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface MethodNodeRepository extends GraphRepository<MethodNode> {
    MethodNode findByName(String name);
}
