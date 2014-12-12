package me.loki2302.impl.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    public Long id;
    public String content;

    @ManyToOne
    public Post post;
}
