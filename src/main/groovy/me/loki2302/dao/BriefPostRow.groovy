package me.loki2302.dao

import groovy.transform.builder.Builder

@Builder
class BriefPostRow {
    long postId
    String postContent
    long postCommentCount
    long userId
    String userName
    long userPostCount
    long userCommentCount
}
