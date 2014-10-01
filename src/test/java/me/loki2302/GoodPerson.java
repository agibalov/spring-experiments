package me.loki2302;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("good")
public class GoodPerson extends Person {
    public int goodnessLevel;
}
