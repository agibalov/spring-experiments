package me.loki2302.dto.mappers

import me.loki2302.dao.comments.CommentRow
import me.loki2302.dao.users.UserRow
import me.loki2302.dto.BriefCommentDTO
import me.loki2302.dto.BriefUserDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentMapper {
    @Autowired
    private UserMapper userMapper

    public BriefCommentDTO makeBriefCommentDTO(
            CommentRow commentRow,
            Map<Long, UserRow> userMap) {

        UserRow commentUserRow = userMap[commentRow.userId]
        BriefUserDTO commentUser = userMapper.makeBriefUserDTO(commentUserRow)

        new BriefCommentDTO(
                id: commentRow.id,
                content: commentRow.content,
                user: commentUser)
    }

    public List<BriefCommentDTO> makeBriefCommentDTOs(
            List<CommentRow> commentRows,
            Map<Long, UserRow> userMap) {

        commentRows.collect {
            makeBriefCommentDTO(it, userMap)
        }
    }
}
