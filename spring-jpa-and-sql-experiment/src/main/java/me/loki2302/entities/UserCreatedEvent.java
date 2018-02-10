package me.loki2302.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("UserCreated")
public class UserCreatedEvent extends Event {
}
