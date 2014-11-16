package me.loki2302.dto

import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty

@ToString(includeNames = true)
class BriefUserDTO {
    long id

    @NotEmpty
    String name

    long postCount
    long commentCount
}
