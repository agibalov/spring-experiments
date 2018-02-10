package me.loki2302.dto

import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.NotNull

@ToString(includeNames = true)
class UserDTO {
    long id

    @NotEmpty
    String name

    long postCount
    long commentCount

    @NotNull
    List<BriefPostDTO> recentPosts

    @NotNull
    List<BriefCommentDTO> recentComments
}
