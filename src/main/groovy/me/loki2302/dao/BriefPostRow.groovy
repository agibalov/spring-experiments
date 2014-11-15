package me.loki2302.dao

import groovy.transform.builder.Builder

@Builder
class BriefPostRow {
    long id
    String content
    long commentCount
    long userId
}
