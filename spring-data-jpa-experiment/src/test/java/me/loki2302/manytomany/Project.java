package me.loki2302.manytomany;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToMany(mappedBy = "projects")
    public Set<Person> persons = new HashSet<Person>();
}
