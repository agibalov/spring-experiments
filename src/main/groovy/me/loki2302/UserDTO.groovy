package me.loki2302
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty

@ToString(includeNames = true)
class UserDTO {
    long id

    @NotEmpty
    String name

    long postCount
    long commentCount
}
