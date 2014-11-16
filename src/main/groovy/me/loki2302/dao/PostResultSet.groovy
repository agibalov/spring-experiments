package me.loki2302.dao

class PostResultSet {
    private final List<PostRow> postRows

    public PostResultSet(List<PostRow> postRows) {
        this.postRows = postRows
    }

    public Set<Long> getPostIds() {
        (postRows*.id).toSet()
    }

    public Set<Long> getUserIds() {
        (postRows*.userId).toSet()
    }

    public List<PostRow> getRows() {
        postRows
    }
}
