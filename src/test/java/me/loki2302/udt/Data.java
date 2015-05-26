package me.loki2302.udt;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@TypeDefs({
        @TypeDef(name = "myUdt", typeClass = IntegerAsVarcharUserType.class),
        @TypeDef(name = "json", typeClass = DTOAsJSONUserType.class)
})
public class Data {
    @Id
    @GeneratedValue
    public Long id;

    @Type(type = "myUdt", parameters = {})
    public Integer someInteger;

    @Type(type = "json")
    public SomeUselessDto someUselessDto;
}
