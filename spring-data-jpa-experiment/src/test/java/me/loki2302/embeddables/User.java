package me.loki2302.embeddables;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue
    public Long id;

    @Embedded
    public Profile profile;

    @ElementCollection
    public List<Interest> interests;
}
