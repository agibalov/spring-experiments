package io.agibalov.querying;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Person {
    @Id
    private String id;
    private String name;
    private int age;
}
