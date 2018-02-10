package me.loki2302.dao.users

import me.loki2302.dao.posts.PostRow

class UserResultSet {
    private final List<UserRow> userRows

    UserResultSet(List<PostRow> userRows) {
        this.userRows = userRows
    }

    Map<Long, UserRow> groupById() {
        userRows.groupBy { it.id }.collectEntries { userId, users -> [userId, users.first()] }
    }

    List<PostRow> getRows() {
        userRows
    }
}
