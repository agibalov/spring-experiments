package me.loki2302.dto

import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.Valid
import javax.validation.constraints.NotNull

@ToString(includeNames = true)
class BriefCommentDTO {
    long id

    @NotEmpty
    String content

    @NotNull
    @Valid
    BriefUserDTO user
}
