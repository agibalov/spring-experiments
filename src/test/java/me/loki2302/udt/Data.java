package me.loki2302.udt;

import me.loki2302.lobs.TheBLOB;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@TypeDef(name = "myUdt", typeClass = IntegerAsVarcharUserType.class)
public class Data {
    @Id
    @GeneratedValue
    public Long id;

    @Type(type = "myUdt", parameters = {})
    public Integer someInteger;
}
