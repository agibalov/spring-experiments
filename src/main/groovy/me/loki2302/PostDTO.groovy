package me.loki2302

import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.Valid
import javax.validation.constraints.NotNull

@ToString(includeNames = true)
class PostDTO {
    long id

    @NotEmpty
    String content

    @NotNull
    @Valid
    BriefUserDTO user

    @NotNull
    List<BriefCommentDTO> comments
}
