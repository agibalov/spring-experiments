package me.loki2302.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long id;

    @Column(unique = true, name = "name")
    public String name;

    @OneToMany(mappedBy = "user")
    public List<Post> posts;
}
