package me.loki2302.onetomany;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Blog {
    @Id
    @GeneratedValue
    public Long id;

    @OneToMany(mappedBy = "blog", fetch = FetchType.EAGER)
    public List<Post> posts = new ArrayList<Post>();
}
