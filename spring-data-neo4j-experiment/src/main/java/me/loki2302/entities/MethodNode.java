package me.loki2302.entities;

import lombok.ToString;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@ToString
@NodeEntity(label = "Method")
public class MethodNode {
    @GraphId
    public Long id;
    public String name;
    public String shortName;

    @Relationship(type = "USES", direction = Relationship.OUTGOING)
    public Set<MethodNode> usedMethods = new HashSet<>();
}
