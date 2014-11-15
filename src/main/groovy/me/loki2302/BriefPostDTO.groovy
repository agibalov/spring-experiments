package me.loki2302

import groovy.transform.builder.Builder
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.Valid
import javax.validation.constraints.NotNull

@Builder
class BriefPostDTO {
    long id

    @NotEmpty
    String content

    long commentCount

    @NotNull
    @Valid
    BriefUserDTO user
}
