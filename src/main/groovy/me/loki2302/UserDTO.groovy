package me.loki2302

import groovy.transform.ToString
import groovy.transform.builder.Builder
import org.hibernate.validator.constraints.NotEmpty

@Builder
@ToString(includeNames = true)
class UserDTO {
    long id

    @NotEmpty
    String name

    long postCount
    long commentCount
}
