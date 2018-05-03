package me.loki2302.converter;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Person {
    @Id
    private String id;

    @Convert(converter = SomeComplexDetailsAsJsonAttributeConverter.class)
    private SomeComplexDetails complexDetails;

    @Convert(converter = SetOfStringAsJsonAttributeConverter.class)
    private Set<String> interests;
}
