package io.agibalov.cascading;

import javax.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    public Blog blog;
}
