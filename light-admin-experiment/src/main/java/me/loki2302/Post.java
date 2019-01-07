package me.loki2302;

import javax.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue
    public Long id;
    public String title;
    public String content;

    @ManyToOne(fetch = FetchType.EAGER)
    public Blog blog;
}
