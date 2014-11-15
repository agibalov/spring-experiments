package me.loki2302

import me.loki2302.dao.CommentDAO
import me.loki2302.dao.CommentRow
import me.loki2302.dao.PostDAO
import me.loki2302.dao.PostRow
import me.loki2302.dao.UserDAO
import me.loki2302.dao.UserRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Facade {
    @Autowired
    UserDAO userDAO

    @Autowired
    PostDAO postDAO

    @Autowired
    CommentDAO commentDAO

    List<PostDTO> getPosts() {
        List<PostRow> postRows = postDAO.all

        Set<Long> uniquePostIds = postRows*.userId.toSet()
        Map<Long, CommentRow> commentRowsGroupedByPostId = getCommentRowsGroupedByPostId(uniquePostIds)

        Set<Long> uniqueUserIds = [].toSet()
        uniqueUserIds.addAll postRows*.userId
        uniqueUserIds.addAll commentRowsGroupedByPostId.collectMany { postId, comments -> comments*.userId }
        Map<Long, UserDTO> userByUserIds = getUsersAndReturnBriefUserDTOByUserIdMap(uniqueUserIds)

        Map<Long, List<CommentDTO>> commentDTOsGroupedByPostId = commentRowsGroupedByPostId.collectEntries { postId, comments ->
            [postId, comments.collect {
                new CommentDTO(
                    id: it.id,
                    content: it.content,
                    user: userByUserIds[it.userId])
            }]
        }

        postRows.collect {
            new PostDTO(
                    id: it.id,
                    content: it.content,
                    commentCount: it.commentCount,
                    user: userByUserIds[it.userId],
                    recentComments: commentDTOsGroupedByPostId[it.id] ?: [])
        }
    }

    private Map<Long, UserDTO> getUsersAndReturnBriefUserDTOByUserIdMap(Set<Long> userIds) {
        Set<UserRow> users = userDAO.findUsers(userIds)
        users.collect {
            new UserDTO(
                    id: it.id,
                    name: it.name,
                    postCount: it.postCount,
                    commentCount: it.commentCount)
        }.collectEntries {
            [it.id, it]
        }
    }

    private Map<Long, CommentRow> getCommentRowsGroupedByPostId(Set<Long> postIds) {
        commentDAO.getRecentCommentsForPosts(postIds, 3).groupBy { it.postId }
    }
}
