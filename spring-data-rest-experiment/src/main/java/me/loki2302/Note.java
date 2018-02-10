package me.loki2302;

import lombok.ToString;

import javax.persistence.*;

@Entity
@ToString
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    public Person person;
}
