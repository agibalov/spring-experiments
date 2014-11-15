package me.loki2302

import groovy.transform.builder.Builder

@Builder
class BriefUserDTO {
    long id
    String name
    long postCount
    long commentCount
}
