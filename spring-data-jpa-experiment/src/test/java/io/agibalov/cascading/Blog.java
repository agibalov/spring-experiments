package io.agibalov.cascading;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Blog {
    @Id
    @GeneratedValue
    public Long id;

    @OneToMany(mappedBy = "blog", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<Post> posts = new ArrayList<Post>();
}
