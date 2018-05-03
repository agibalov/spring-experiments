package io.agibalov.lobs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.sql.Blob;

@Entity
public class LargeDocument {
    @Id
    @GeneratedValue
    public Long id;

    @Lob
    public Blob data;
}
