package me.loki2302.dao

import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentDAO {
    @Autowired
    private Sql sql

    CommentResultSet findRecentCommentsForPosts(Set<Long> postIds, int topCommentCount) {
        def rows = findRecentCommentsForPostsAsRows(postIds, topCommentCount)
        new CommentResultSet(rows)
    }

    CommentResultSet findRecentCommentsByUser(long userId, int topCommentCount) {
        def rows = findRecentCommentsByUserAsRows(userId, topCommentCount)
        new CommentResultSet(rows)
    }

    private List<CommentRow> findRecentCommentsForPostsAsRows(Set<Long> postIds, int topCommentCount) {
        sql.rows("""
            select
                C.id,
                C.content,
                C.userId,
                C.postId
            from Comments as C
            where
                C.postId in (""" + postIds.join(',') + """) and
                C.id in (
                    select top $topCommentCount id
                    from Comments
                    where postId = C.postId
                    order by id desc)
            order by C.postId asc, C.id desc
        """).collect {
            new CommentRow(
                    id: it.id,
                    content: it.content,
                    userId: it.userId,
                    postId: it.postId)
        }
    }

    private List<PostRow> findRecentCommentsByUserAsRows(long userId, int topCommentCount) {
        sql.rows("""
            select top $topCommentCount
                id,
                content,
                userId,
                postId
            from Comments
            where userId = $userId
            order by id desc
        """).collect {
            new CommentRow(
                    id: it.id,
                    content: it.content,
                    userId: it.userId,
                    postId: it.postId)
        }
    }
}
