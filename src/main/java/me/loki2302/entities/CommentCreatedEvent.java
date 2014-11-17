package me.loki2302.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("CommentCreated")
public class CommentCreatedEvent extends Event {
    @ManyToOne(optional = false)
    @JoinColumn(name = "commentId")
    public Comment comment;
}
