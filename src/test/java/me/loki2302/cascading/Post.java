package me.loki2302.cascading;

import javax.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    public Blog blog;
}
