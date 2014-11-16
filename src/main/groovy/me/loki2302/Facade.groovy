package me.loki2302

import me.loki2302.dao.*
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

    PostDTO getPost(long id) {
        throw new RuntimeException("Not implemented") // TODO
    }

    UserDTO findUser(long userId) {
        UserRow userRow = userDAO.findUser(userId)
        if(userRow == null) {
            return null
        }

        PostResultSet usersRecentPosts = postDAO.findRecentByUser(userId, 3)
        CommentResultSet recentCommentsForUsersRecentPosts = commentDAO.findRecentCommentsForPosts(usersRecentPosts.postIds, 3)
        CommentResultSet usersRecentComments = commentDAO.findRecentCommentsByUser(userId, 3)

        Set<Long> referencedUserIds = [].toSet() +
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
