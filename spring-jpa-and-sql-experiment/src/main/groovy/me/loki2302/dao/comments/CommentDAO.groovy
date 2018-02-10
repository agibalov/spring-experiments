package me.loki2302.dao.comments

import groovy.sql.Sql
import me.loki2302.dao.posts.PostRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentDAO {
    @Autowired
    private Sql sql

    CommentResultSet findCommentsForPost(long postId) {
        def rows = findCommentsForPostAsRows(postId)
        new CommentResultSet(rows)
    }

    CommentResultSet findRecentCommentsForPosts(Set<Long> postIds, int topCommentCount) {
        def rows = findRecentCommentsForPostsAsRows(postIds, topCommentCount)
        new CommentResultSet(rows)
    }

    CommentResultSet findRecentCommentsByUser(long userId, int topCommentCount) {
        def rows = findRecentCommentsByUserAsRows(userId, topCommentCount)
        new CommentResultSet(rows)
    }

    private List<CommentRow> findCommentsForPostAsRows(long postId) {
        sql.rows("""
            select
                C.id,
                C.content,
                C.userId,
                C.postId
            from Comments as C
            where C.postId = $postId
            order by C.id asc
        """).collect {
            new CommentRow(
                    id: it.id,
                    content: it.content,
                    userId: it.userId,
                    postId: it.postId)
        }
    }

    private List<CommentRow> findRecentCommentsForPostsAsRows(Set<Long> postIds, int topCommentCount) {
        if(postIds.empty) {
            return []
        }

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
                    select id
                    from Comments
                    where postId = C.postId
                    order by id desc
                    limit $topCommentCount)
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
            select
                id,
                content,
                userId,
                postId
            from Comments
            where userId = $userId
            order by id desc
            limit $topCommentCount
        """).collect {
            new CommentRow(
                    id: it.id,
                    content: it.content,
                    userId: it.userId,
                    postId: it.postId)
        }
    }
}
