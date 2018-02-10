package me.loki2302.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("PostCreated")
public class PostCreatedEvent extends Event {
    @ManyToOne(optional = false)
    @JoinColumn(name = "postId")
    public Post post;
}
