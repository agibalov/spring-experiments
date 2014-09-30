package me.loki2302;

import javax.persistence.*;
import java.util.List;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String name;

    @OneToMany(mappedBy = "person")
    public List<Note> notes;

    @Override
    public String toString() {
        return String.format("Person{id=%s, name=%s}", id, name);
    }
}
