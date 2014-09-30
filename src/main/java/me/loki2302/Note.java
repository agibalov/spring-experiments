package me.loki2302;

import javax.persistence.*;

@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    public Person person;
}
