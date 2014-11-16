package me.loki2302.dto.mappers

import me.loki2302.dao.comments.CommentRow
import me.loki2302.dao.posts.PostRow
import me.loki2302.dao.users.UserRow
import me.loki2302.dto.BriefCommentDTO
import me.loki2302.dto.BriefPostDTO
import me.loki2302.dto.BriefUserDTO
import me.loki2302.dto.UserDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserMapper {
    @Autowired
    private PostMapper postMapper

    @Autowired
    private CommentMapper commentMapper

    public BriefUserDTO makeBriefUserDTO(UserRow userRow) {
        new BriefUserDTO(
                id: userRow.id,
                name: userRow.name,
                postCount: userRow.postCount,
                commentCount: userRow.commentCount)
    }

    public UserDTO makeUserDTO(
            UserRow userRow,
            Map<Long, UserRow> userMap,
            List<PostRow> recentPostRows,
            Map<Long, CommentRow> commentsForRecentPostsMap,
            List<CommentRow> recentCommentRows) {

        List<BriefPostDTO> recentPosts = postMapper.makeBriefPostDTOs(
                recentPostRows,
                userMap,
                commentsForRecentPostsMap)

        List<BriefCommentDTO> recentComments = commentMapper.makeBriefCommentDTOs(
                recentCommentRows,
                userMap)

        new UserDTO(
                id: userRow.id,
                name: userRow.name,
                postCount: userRow.postCount,
                commentCount: userRow.commentCount,
                recentPosts: recentPosts,
                recentComments: recentComments)
    }
}
