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

    List<BriefPostDTO> getPosts() {
        PostResultSet postResultSet = postDAO.findAll()

        Set<Long> postIds = postResultSet.getPostIds()
        CommentResultSet commentResultSet = commentDAO.findRecentCommentsForPosts(postIds, 3)

        Set<Long> uniqueUserIds = postResultSet.userIds + commentResultSet.userIds
        UserResultSet userResultSet = userDAO.findUsers(uniqueUserIds)

        postMapper.makeBriefPostDTOs(
                postResultSet.rows,
                userResultSet.groupById(),
                commentResultSet.groupByPostId())
    }

    PostDTO getPost(long id) {
        throw new RuntimeException("Not implemented") // TODO
    }

    UserDTO getUser(long userId) {
        UserRow userRow = userDAO.findUser(userId)
        if(userRow == null) {
            throw new RuntimeException("No such user")
        }

        PostResultSet usersRecentPostsResultSet = postDAO.findRecentByUser(userId, 3)
        CommentResultSet recentCommentsForUsersRecentPostsResultSet = commentDAO.findRecentCommentsForPosts(usersRecentPostsResultSet.postIds, 3)
        CommentResultSet usersRecentCommentsResultSet = commentDAO.findRecentCommentsByUser(userId, 3)

        Set<Long> uniqueUsersIds = [].toSet() +
                usersRecentPostsResultSet.userIds +
                recentCommentsForUsersRecentPostsResultSet.userIds +
                usersRecentCommentsResultSet.userIds
        UserResultSet userResultSet = userDAO.findUsers(uniqueUsersIds)

        userMapper.makeUserDTO(
                userRow,
                userResultSet.groupById(),
                usersRecentPostsResultSet.rows,
                recentCommentsForUsersRecentPostsResultSet.groupByPostId(),
                usersRecentCommentsResultSet.rows)
    }
}
