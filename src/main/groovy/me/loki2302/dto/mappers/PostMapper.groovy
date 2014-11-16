package me.loki2302.dto.mappers

import me.loki2302.dao.comments.CommentRow
import me.loki2302.dao.posts.PostRow
import me.loki2302.dao.users.UserRow
import me.loki2302.dto.BriefCommentDTO
import me.loki2302.dto.BriefPostDTO
import me.loki2302.dto.BriefUserDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostMapper {
    @Autowired
    private UserMapper userMapper

    @Autowired
    private CommentMapper commentMapper

    public BriefPostDTO makeBriefPostDTO(
            PostRow postRow,
            Map<Long, UserRow> userMap,
            Map<Long, List<CommentRow>> recentCommentsMap) {

        UserRow postUser = userMap[postRow.userId]
        BriefUserDTO userDTO = userMapper.makeBriefUserDTO(postUser)

        List<CommentRow> postComments = recentCommentsMap[postRow.id] ?: []
        List<BriefCommentDTO> recentCommentDTOs = commentMapper.makeBriefCommentDTOs(postComments, userMap)

        new BriefPostDTO(
                id: postRow.id,
                content: postRow.content,
                commentCount: postRow.commentCount,
                user: userDTO,
                recentComments: recentCommentDTOs)
    }

    public List<BriefPostDTO> makeBriefPostDTOs(
            List<PostRow> postRows,
            Map<Long, UserRow> userMap,
            Map<Long, List<CommentRow>> recentCommentsMap) {

        postRows.collect {
            makeBriefPostDTO(it, userMap, recentCommentsMap)
        }
    }
}
