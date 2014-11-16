package me.loki2302

import me.loki2302.dao.CommentDAO
import me.loki2302.dao.CommentResultSet
import me.loki2302.dao.CommentRow
import me.loki2302.dao.PostDAO
import me.loki2302.dao.PostResultSet
import me.loki2302.dao.PostRow
import me.loki2302.dao.UserDAO
import me.loki2302.dao.UserResultSet
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

    private static BriefUserDTO makeBriefUserDTO(UserRow userRow) {
        new BriefUserDTO(
                id: userRow.id,
                name: userRow.name,
                postCount: userRow.postCount,
                commentCount: userRow.commentCount)
    }

    private static UserDTO makeUserDTO(
            UserRow userRow,
            Map<Long, UserRow> userMap,
            List<PostRow> recentPostRows,
            Map<Long, CommentRow> commentsForRecentPostsMap,
            List<CommentRow> recentCommentRows) {

        List<BriefPostDTO> recentPosts = makeBriefPostDTOs(recentPostRows, userMap, commentsForRecentPostsMap)
        List<BriefCommentDTO> recentComments = makeBriefCommentDTOs(recentCommentRows, userMap)

        new UserDTO(
                id: userRow.id,
                name: userRow.name,
                postCount: userRow.postCount,
                commentCount: userRow.commentCount,
                recentPosts: recentPosts,
                recentComments: recentComments)
    }

    private static BriefCommentDTO makeBriefCommentDTO(
            CommentRow commentRow,
            Map<Long, UserRow> userMap) {

        new BriefCommentDTO(
                id: commentRow.id,
                content: commentRow.content,
                user: makeBriefUserDTO(userMap[commentRow.userId]))
    }

    private static List<BriefCommentDTO> makeBriefCommentDTOs(List<CommentRow> commentRows, Map<Long, UserRow> userMap) {
        commentRows.collect { makeBriefCommentDTO(it, userMap) }
    }

    private static BriefPostDTO makeBriefPostDTO(
            PostRow postRow,
            Map<Long, UserRow> userMap,
            Map<Long, List<CommentRow>> recentCommentsMap) {

        BriefUserDTO userDTO = makeBriefUserDTO(userMap[postRow.userId])
        List<BriefCommentDTO> recentCommentDTOs = makeBriefCommentDTOs(recentCommentsMap[postRow.id] ?: [], userMap)

        new BriefPostDTO(
                id: postRow.id,
                content: postRow.content,
                commentCount: postRow.commentCount,
                user: userDTO,
                recentComments: recentCommentDTOs)
    }

    private static List<BriefPostDTO> makeBriefPostDTOs(
            List<PostRow> postRows,
            Map<Long, UserRow> userMap,
            Map<Long, List<CommentRow>> recentCommentsMap) {

        postRows.collect { makeBriefPostDTO(it, userMap, recentCommentsMap) }
    }

    List<BriefPostDTO> getPosts() {
        PostResultSet postResultSet = postDAO.findAll()

        Set<Long> postIds = postResultSet.getPostIds()
        CommentResultSet commentResultSet = commentDAO.findRecentCommentsForPosts(postIds, 3)

        Set<Long> uniqueUserIds = postResultSet.userIds + commentResultSet.userIds
        UserResultSet userResultSet = userDAO.findUsers(uniqueUserIds)

        Map<Long, UserRow> userMap = userResultSet.groupById()
        Map<Long, List<CommentRow>> commentListMap = commentResultSet.groupByPostId()

        postResultSet.rows.collect {
            makeBriefPostDTO(it, userMap, commentListMap)
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

        PostResultSet usersRecentPostsResultSet = postDAO.findRecentByUser(userId, 3)
        CommentResultSet recentCommentsForUsersRecentPostsResultsSet = commentDAO.findRecentCommentsForPosts(usersRecentPostsResultSet.postIds, 3)
        CommentResultSet usersRecentCommentsResultsSet = commentDAO.findRecentCommentsByUser(userId, 3)

        Set<Long> uniqueUsersIds = [].toSet() +
                usersRecentPostsResultSet.userIds +
                recentCommentsForUsersRecentPostsResultsSet.userIds +
                usersRecentCommentsResultsSet.userIds
        UserResultSet userResultSet = userDAO.findUsers(uniqueUsersIds)

        makeUserDTO(
                userRow,
                userResultSet.groupById(),
                usersRecentPostsResultSet.rows,
                recentCommentsForUsersRecentPostsResultsSet.groupByPostId(),
                usersRecentCommentsResultsSet.rows)
    }
}
