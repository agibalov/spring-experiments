package me.loki2302
import groovy.sql.Sql
import me.loki2302.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserDAO {
    @Autowired
    Sql sql

    @Autowired
    UserRepository userRepository

    UserDTO findUser(long id) {
        def userRow = sql.firstRow("""
            select
                U.id, U.name,
                (select count(P.id) from Posts as P where P.userId = U.id) as postCount,
                (select count(C.id) from Comments as C where C.userId = U.id) as commentCount
            from Users as U
            where U.id = $id
        """)

        if(userRow == null) {
            return null
        }

        UserDTO.builder()
                .id(userRow.id)
                .name(userRow.name)
                .postCount(userRow.postCount)
                .commentCount(userRow.commentCount)
                .build()
    }
}
