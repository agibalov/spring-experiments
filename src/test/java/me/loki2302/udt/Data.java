package me.loki2302.udt;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

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

    // TODO: can I replace this with my custom annotation, like @Json?
    // TODO: how do I force varchar(max) from within DTOAsJSONUserType?
    @Type(type = "json", parameters = {
            @Parameter(name = "className", value = "me.loki2302.udt.SomeUselessDto")
    })
    public SomeUselessDto someUselessDto;
}
