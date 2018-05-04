package io.agibalov.version;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Note {
    @Id
    public Long id;

    @Version
    public Long version;

    public String content;
}
