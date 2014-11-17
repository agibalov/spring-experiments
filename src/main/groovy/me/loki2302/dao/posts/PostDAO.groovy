package me.loki2302.dao.posts

import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostDAO {
    @Autowired
    private Sql sql

    PostResultSet findAll() {
        def rows = findAllAsRows()
        new PostResultSet(rows)
    }

    PostResultSet findRecentByUser(long userId, int topPostCount) {
        def rows = findRecentByUserAsRows(userId, topPostCount)
        new PostResultSet(rows)
    }

    PostRow findById(long postId) {
        def row = sql.firstRow("""
            select
                P.id as id,
                P.content as content,
                (select count(C.id) from Comments as C where C.postId = P.id) as commentCount,
                P.userId as userId
            from Posts as P
            where P.id = $postId
        """)

        if(row == null) {
            return null
        }

        new PostRow(
                id: row.id,
                content: row.content,
                commentCount: row.commentCount,
                userId: row.userId)
    }

    private List<PostRow> findAllAsRows() {
        sql.rows("""
            select
                P.id as id,
                P.content as content,
                (select count(C.id) from Comments as C where C.postId = P.id) as commentCount,
                P.userId as userId
            from Posts as P
            order by P.id desc
        """).collect {
            new PostRow(
                    id: it.id,
                    content: it.content,
                    commentCount: it.commentCount,
                    userId: it.userId)
        }
    }

    private List<PostRow> findRecentByUserAsRows(long userId, int topPostCount) {
        sql.rows("""
            select top $topPostCount
                P.id as id,
                P.content as content,
                (select count(C.id) from Comments as C where C.postId = P.id) as commentCount,
                P.userId as userId
            from Posts as P
            where userId = $userId
            order by P.id desc
        """).collect {
            new PostRow(
                id: it.id,
                content: it.content,
                commentCount: it.commentCount,
                userId: it.userId)
        }
    }
}
