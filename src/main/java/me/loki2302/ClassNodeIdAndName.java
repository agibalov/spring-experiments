package me.loki2302;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ClassNodeIdAndName {
    public Long id;
    public String name;
}
