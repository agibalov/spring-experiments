package me.loki2302;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class ClassNode {
    @GraphId
    public Long id;
    public String name;

    @Relationship(type = "USES", direction = Relationship.OUTGOING)
    public Set<ClassNode> usedClasses = new HashSet<>();
}
