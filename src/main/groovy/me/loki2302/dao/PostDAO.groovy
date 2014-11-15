package me.loki2302.dao
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostDAO {
    @Autowired
    Sql sql

    List<BriefPostRow> getAll() {
        def postRows = sql.rows("""
select
    P.id as postId, P.content as postContent,
    (select count(C.id) from Comments as C where C.postId = P.id) as postCommentCount,
    U.id as userId, U.name as userName,
    (select count(UP.id) from Posts as UP where UP.userId = U.id) as userPostCount,
    (select count(UC.id) from Comments as UC where UC.userId = U.id) as userCommentCount
from Posts as P
join Users as U on U.id = P.userId
order by P.id
""")

        postRows.collect {
            BriefPostRow.builder()
                .postId(it.postId)
                .postContent(it.postContent)
                .postCommentCount(it.postCommentCount)
                .userId(it.userId)
                .userName(it.userName)
                .userPostCount(it.userPostCount)
                .userCommentCount(it.userCommentCount)
        }
    }
}
