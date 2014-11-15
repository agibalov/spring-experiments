package me.loki2302.dao

import groovy.transform.builder.Builder

@Builder
class BriefUserRow {
    long id
    String name
    long postCount
    long commentCount
}
