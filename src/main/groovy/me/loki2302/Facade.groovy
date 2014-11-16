package me.loki2302

import me.loki2302.dao.comments.CommentDAO
import me.loki2302.dao.comments.CommentResultSet
import me.loki2302.dao.posts.PostDAO
import me.loki2302.dao.posts.PostResultSet
import me.loki2302.dao.posts.PostRow
import me.loki2302.dao.users.UserDAO
import me.loki2302.dao.users.UserResultSet
import me.loki2302.dao.users.UserRow
import me.loki2302.dto.BriefPostDTO
import me.loki2302.dto.PostDTO
import me.loki2302.dto.UserDTO
import me.loki2302.dto.mappers.PostMapper
import me.loki2302.dto.mappers.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Facade {
    @Autowired
    private UserDAO userDAO

    @Autowired
    private PostDAO postDAO

    @Autowired
    private CommentDAO commentDAO

    @Autowired
    private PostMapper postMapper

    @Autowired
    private UserMapper userMapper

    List<BriefPostDTO> findAllPosts() {
        PostResultSet posts = postDAO.findAll()

        Set<Long> postIds = posts.getPostIds()
        CommentResultSet recentCommentsForPosts = commentDAO.findRecentCommentsForPosts(postIds, 3)

        Set<Long> referencedUserIds = posts.userIds + recentCommentsForPosts.userIds
        UserResultSet referencedUsers = userDAO.findUsers(referencedUserIds)

        postMapper.makeBriefPostDTOs(
                posts.rows,
                referencedUsers.groupById(),
                recentCommentsForPosts.groupByPostId())
    }

    PostDTO findPost(long postId) {
        PostRow postRow = postDAO.findById(postId)
        if(postRow == null) {
            return null
        }

        CommentResultSet postComments = commentDAO.findCommentsForPost(postId)

        Set<Long> referencedUserIds = Collections.singleton(postRow.userId) + postComments.userIds
        UserResultSet referencedUsers = userDAO.findUsers(referencedUserIds)

        postMapper.makePostDTO(
                postRow,
                referencedUsers.groupById(),
                postComments.rows)
    }

    UserDTO findUser(long userId) {
        UserRow userRow = userDAO.findUser(userId)
        if(userRow == null) {
            return null
        }

        PostResultSet usersRecentPosts = postDAO.findRecentByUser(userId, 3)
        CommentResultSet recentCommentsForUsersRecentPosts = commentDAO.findRecentCommentsForPosts(usersRecentPosts.postIds, 3)
        CommentResultSet usersRecentComments = commentDAO.findRecentCommentsByUser(userId, 3)

        Set<Long> referencedUserIds =
                usersRecentPosts.userIds +
                recentCommentsForUsersRecentPosts.userIds +
                usersRecentComments.userIds
        UserResultSet referencedUsers = userDAO.findUsers(referencedUserIds)

        userMapper.makeUserDTO(
                userRow,
                referencedUsers.groupById(),
                usersRecentPosts.rows,
                recentCommentsForUsersRecentPosts.groupByPostId(),
                usersRecentComments.rows)
    }
}
