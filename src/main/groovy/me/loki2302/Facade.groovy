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

    List<BriefPostDTO> getPosts() {
        List<PostRow> postRows = postDAO.all

        Set<Long> uniquePostIds = postRows*.userId.toSet()
        Map<Long, CommentRow> commentRowsGroupedByPostId = getCommentRowsGroupedByPostId(uniquePostIds)

        Set<Long> uniqueUserIds = [].toSet()
        uniqueUserIds.addAll postRows*.userId
        uniqueUserIds.addAll commentRowsGroupedByPostId.collectMany { postId, comments -> comments*.userId }
        Map<Long, BriefUserDTO> userByUserIds = getUsersAndReturnBriefUserDTOByUserIdMap(uniqueUserIds)

        Map<Long, List<BriefCommentDTO>> commentDTOsGroupedByPostId = commentRowsGroupedByPostId.collectEntries { postId, comments ->
            [postId, comments.collect {
                new BriefCommentDTO(
                    id: it.id,
                    content: it.content,
                    user: userByUserIds[it.userId])
            }]
        }

        postRows.collect {
            new BriefPostDTO(
                    id: it.id,
                    content: it.content,
                    commentCount: it.commentCount,
                    user: userByUserIds[it.userId],
                    recentComments: commentDTOsGroupedByPostId[it.id] ?: [])
        }
    }

    PostDTO getPost(long id) {
        throw new RuntimeException("Not implemented") // TODO
    }

    UserDTO getUser(long userId) {
        UserRow userRow = userDAO.findUser(userId)
        if(userRow == null) {
            throw new RuntimeException("No such user")
        }

        List<PostRow> recentPostRows = postDAO.getRecentPostsByUser(userId, 3)
        List<CommentRow> recentPostCommentRows = commentDAO.getRecentCommentsForPosts(recentPostRows*.id.toSet(), 3)
        List<CommentRow> recentCommentRows = commentDAO.getRecentCommentsByUser(userId, 3)

        BriefUserDTO briefUser = new BriefUserDTO(
                id: userRow.id,
                name: userRow.name,
                postCount: userRow.postCount,
                commentCount: userRow.commentCount)

        Set<Long> recentPostCommentUserIds = recentPostCommentRows*.userId.toSet()
        Map<Long, BriefUserDTO> recentPostCommentUsersMap = getUsersAndReturnBriefUserDTOByUserIdMap(recentPostCommentUserIds)
        Map<Long, List<BriefCommentDTO>> recentPostComments = recentPostCommentRows.groupBy {
            it.postId
        }.collectEntries { postId, comments ->
            [postId, comments.collect {
                new BriefCommentDTO(
                        id: it.id,
                        content: it.content,
                        user: recentPostCommentUsersMap[it.userId])
            }]
        }

        List<BriefPostDTO> recentPosts = recentPostRows.collect {
            new BriefPostDTO(
                    id: it.id,
                    content: it.content,
                    user: briefUser,
                    commentCount: it.commentCount,
                    recentComments: recentPostComments[it.id] ?: [])
        }

        List<BriefCommentDTO> recentComments = recentCommentRows.collect {
            new BriefCommentDTO(
                    id: it.id,
                    content: it.content,
                    user: briefUser)
        }

        new UserDTO(
                id: userRow.id,
                name: userRow.name,
                postCount: userRow.postCount,
                commentCount: userRow.commentCount,
                recentPosts: recentPosts,
                recentComments: recentComments)
    }

    private Map<Long, BriefUserDTO> getUsersAndReturnBriefUserDTOByUserIdMap(Set<Long> userIds) {
        Set<UserRow> users = userDAO.findUsers(userIds)
        users.collect {
            new BriefUserDTO(
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
