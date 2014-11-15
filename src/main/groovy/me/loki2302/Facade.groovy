package me.loki2302

import me.loki2302.dao.BriefPostRow
import me.loki2302.dao.BriefUserRow
import me.loki2302.dao.PostDAO
import me.loki2302.dao.UserDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Facade {
    @Autowired
    UserDAO userDAO

    @Autowired
    PostDAO postDAO

    List<BriefPostDTO> getPosts() {
        List<BriefPostRow> rows = postDAO.all
        Set<Long> uniqueUserIds = rows.collect { it.userId }.toSet()

        Map<Long, BriefUserDTO> usersByIds = getUserMap(uniqueUserIds)

        rows.collect {
            BriefPostDTO.builder()
                .id(it.id)
                .content(it.content)
                .commentCount(it.commentCount)
                .user(usersByIds[it.userId])
                .build()
        }
    }

    private Map<Long, BriefUserDTO> getUserMap(Set<Long> userIds) {
        Set<BriefUserRow> users = userDAO.findUsers(userIds)
        users.collect {
            BriefUserDTO.builder()
                    .id(it.id)
                    .name(it.name)
                    .postCount(it.postCount)
                    .commentCount(it.commentCount)
                    .build()
        }.collectEntries {
            [it.id, it]
        }
    }
}
