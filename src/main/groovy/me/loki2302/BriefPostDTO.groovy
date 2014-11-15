package me.loki2302

import groovy.transform.builder.Builder

@Builder
class BriefPostDTO {
    long id
    String content
    long commentCount
    BriefUserDTO user
}
