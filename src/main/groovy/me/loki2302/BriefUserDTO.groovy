package me.loki2302

import groovy.transform.builder.Builder
import org.hibernate.validator.constraints.NotEmpty

@Builder
class BriefUserDTO {
    long id

    @NotEmpty
    String name

    long postCount
    long commentCount
}
