package me.loki2302.dao

import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostDAO {
    @Autowired
    Sql sql

    List<PostRow> getAll() {
        sql.rows("""
            select
                P.id as id, P.content as content,
                (select count(C.id) from Comments as C where C.postId = P.id) as commentCount,
                P.userId as userId
            from Posts as P
            order by P.id
        """).collect {
            new PostRow(
                id: it.id,
                content: it.content,
                commentCount: it.commentCount,
                userId: it.userId)
        }
    }
}
