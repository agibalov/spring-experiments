package me.loki2302.cypher;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Person {
    public static final String NAME_INDEX = "name-index";
    
    @GraphId
    private Long id;
    
    @Indexed(indexName = NAME_INDEX)
    private String name;    
    private int age;
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
}