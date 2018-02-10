package me.loki2302.dao.posts

class PostResultSet {
    private final List<PostRow> postRows

    PostResultSet(List<PostRow> postRows) {
        this.postRows = postRows
    }

    Set<Long> getPostIds() {
        (postRows*.id).toSet()
    }

    Set<Long> getUserIds() {
        (postRows*.userId).toSet()
    }

    List<PostRow> getRows() {
        postRows
    }
}
