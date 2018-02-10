package me.loki2302.version;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Note {
    @Id
    @GeneratedValue
    public Long id;

    @Version
    public Long version;

    public String content;
}
