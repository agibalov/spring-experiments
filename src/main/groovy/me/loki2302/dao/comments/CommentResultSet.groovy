package me.loki2302.dao.comments

import me.loki2302.dao.posts.PostRow

class CommentResultSet {
    private final List<CommentRow> commentRows

    public CommentResultSet(List<CommentRow> commentRows) {
        this.commentRows = commentRows
    }

    public Set<Long> getUserIds() {
        (commentRows*.userId).toSet()
    }

    public Map<Long, List<CommentRow>> groupByPostId() {
        commentRows.groupBy { it.postId }
    }

    public List<PostRow> getRows() {
        commentRows
    }
}
