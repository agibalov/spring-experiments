package io.agibalov.polymorphic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("bad")
public class BadPerson extends Person {
    public int badnessLevel;
}