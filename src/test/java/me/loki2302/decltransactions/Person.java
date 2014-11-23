package me.loki2302.decltransactions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {
    @Id
    @GeneratedValue
    public Long id;
    public String name;
}
