package me.loki2302.dao

import groovy.transform.builder.Builder

@Builder
class UserRow {
    long id
    String name
    long postCount
    long commentCount
}
