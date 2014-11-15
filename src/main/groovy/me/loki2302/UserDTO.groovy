package me.loki2302

import groovy.transform.builder.Builder

@Builder
class UserDTO {
    long id
    String name
    long postCount
    long commentCount
}
