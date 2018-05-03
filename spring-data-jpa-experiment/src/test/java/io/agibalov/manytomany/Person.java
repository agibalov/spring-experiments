package io.agibalov.manytomany;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Person {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    public Set<Project> projects = new HashSet<Project>();
}
