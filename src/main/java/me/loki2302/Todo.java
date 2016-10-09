package me.loki2302;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Todo {
    @GraphId
    public Long id;
    public String text;

    @Relationship(type = "RELATED_TO", direction = Relationship.UNDIRECTED)
    public Set<Todo> relatedTodos = new HashSet<>();
}
