package me.loki2302;

import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String name;

    @OneToMany(mappedBy = "person")
    public List<Note> notes;
}
