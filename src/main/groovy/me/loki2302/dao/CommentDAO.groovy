package me.loki2302.dao
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentDAO {
    @Autowired
    Sql sql

    List<CommentRow> getRecentCommentsForPosts(Set<Long> postIds, int topCommentCount) {
        def commentRows = sql.rows("""
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
""")

        commentRows.collect {
            CommentRow.builder()
                .id(it.id)
                .content(it.content)
                .userId(it.userId)
                .postId(it.postId)
                .build()
        }
    }
}
