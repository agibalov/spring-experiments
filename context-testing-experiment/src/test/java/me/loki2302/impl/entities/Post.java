package me.loki2302.impl.entities;

import javax.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue
    public Long id;
    public String content;

    @ManyToOne
    public User user;
}
