package me.loki2302.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "content")
    public String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    public User user;

    @OneToMany(mappedBy = "post")
    public List<Comment> comments;
}
