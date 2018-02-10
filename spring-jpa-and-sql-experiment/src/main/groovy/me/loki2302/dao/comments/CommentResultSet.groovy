package me.loki2302.dao.comments

import me.loki2302.dao.posts.PostRow

class CommentResultSet {
    private final List<CommentRow> commentRows

    CommentResultSet(List<CommentRow> commentRows) {
        this.commentRows = commentRows
    }

    Set<Long> getUserIds() {
        (commentRows*.userId).toSet()
    }

    Map<Long, List<CommentRow>> groupByPostId() {
        commentRows.groupBy { it.postId }
    }

    List<PostRow> getRows() {
        commentRows
    }
}
