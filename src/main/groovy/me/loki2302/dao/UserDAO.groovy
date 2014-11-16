package me.loki2302.dao

import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserDAO {
    @Autowired
    private Sql sql

    UserRow findUser(long id) {
        def rows = findUsersAsRows([id].toSet())
        if(rows.isEmpty()) {
            return null
        }

        rows.first()
    }

    UserResultSet findUsers(Set<Long> ids) {
        def rows = findUsersAsRows(ids)
        new UserResultSet(rows)
    }

    private List<UserRow> findUsersAsRows(Set<Long> ids) {
        // the join() thing is https://jira.codehaus.org/browse/GROOVY-5436
        sql.rows("""
            select
                U.id, U.name,
                (select count(P.id) from Posts as P where P.userId = U.id) as postCount,
                (select count(C.id) from Comments as C where C.userId = U.id) as commentCount
            from Users as U
            where U.id in (""" + (ids.join(',')) + """)
        """).collect {
            new UserRow(
                    id: it.id,
                    name: it.name,
                    postCount: it.postCount,
                    commentCount: it.commentCount)
        }
    }
}
