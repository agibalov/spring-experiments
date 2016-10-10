package me.loki2302.entities;

import lombok.ToString;
import org.springframework.data.neo4j.annotation.QueryResult;

@ToString
@QueryResult
public class ClassNodeIdAndName {
    public Long id;
    public String name;
}
