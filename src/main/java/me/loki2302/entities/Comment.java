package me.loki2302.entities;

import javax.persistence.*;

@Entity
@Table(name = "Comments")
public class Comment {
    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long id;

    @Column(name = "content")
    public String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "postId")
    public Post post;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    public User user;
}
