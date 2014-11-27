package me.loki2302.entitylistener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@EntityListeners(NoteEntityListener.class)
public class Note {
    @Id
    @GeneratedValue
    public Long id;

    public String content;
}
