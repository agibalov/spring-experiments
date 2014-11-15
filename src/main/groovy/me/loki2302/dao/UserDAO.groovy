package me.loki2302.dao
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserDAO {
    @Autowired
    Sql sql

    BriefUserRow findUser(long id) {
        def users = findUsers([id].toSet())
        if(users.isEmpty()) {
            return null
        }

        users.first()
    }

    List<BriefUserRow> findUsers(Set<Long> ids) {
        def userRows = sql.rows("""
            select
                U.id, U.name,
                (select count(P.id) from Posts as P where P.userId = U.id) as postCount,
                (select count(C.id) from Comments as C where C.userId = U.id) as commentCount
            from Users as U
            where U.id in (${ids.join(',')})
        """)

        userRows.collect {
            BriefUserRow.builder()
                    .id(it.id)
                    .name(it.name)
                    .postCount(it.postCount)
                    .commentCount(it.commentCount)
                    .build()
        }
    }
}
