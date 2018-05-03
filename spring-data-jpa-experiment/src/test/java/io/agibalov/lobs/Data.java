package io.agibalov.lobs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Data {
    @Id
    @GeneratedValue
    public Long id;

    @Lob
    public String clob;

    @Lob
    public TheBLOB blob;
}
