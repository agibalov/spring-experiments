package me.loki2302.dao

import groovy.transform.builder.Builder

@Builder
class CommentRow {
    long id
    String content
    long postId
    long userId
}
