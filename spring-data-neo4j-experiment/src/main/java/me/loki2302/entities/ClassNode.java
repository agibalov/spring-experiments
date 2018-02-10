package me.loki2302.entities;

import lombok.ToString;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@ToString
@NodeEntity(label = "Class")
public class ClassNode {
    @GraphId
    public Long id;
    public String name;
    public String shortName;

    @Relationship(type = "USES", direction = Relationship.OUTGOING)
    public Set<ClassNode> usedClasses = new HashSet<>();

    @Relationship(type = "HAS_METHOD", direction = Relationship.OUTGOING)
    public Set<MethodNode> ownedMethods = new HashSet<>();
}
