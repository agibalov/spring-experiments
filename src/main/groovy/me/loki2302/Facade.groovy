package me.loki2302

import me.loki2302.dao.BriefPostRow
import me.loki2302.dao.PostDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Facade {
    @Autowired
    PostDAO postDAO

    List<BriefPostDTO> getPosts() {
        List<BriefPostRow> rows = postDAO.all
        rows.collect {
            BriefPostDTO.builder()
                .id(it.postId)
                .content(it.postContent)
                .commentCount(it.postCommentCount)
                .user(BriefUserDTO.builder()
                    .id(it.userId)
                    .name(it.userName)
                    .postCount(it.userPostCount)
                    .commentCount(it.userCommentCount)
                    .build())
                .build()
        }
    }
}
