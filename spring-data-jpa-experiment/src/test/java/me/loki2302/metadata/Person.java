package me.loki2302.metadata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {
    @Id
    @GeneratedValue
    public Long id;
    public String name;
    public int age;
}
