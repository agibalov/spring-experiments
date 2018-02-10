package me.loki2302.jdbc;

import java.util.List;

public interface UserDao {
    UserRow createUser(String userName);
    UserRow findUser(int userId);
    List<UserRow> findUsers(List<Integer> userIds);
    List<UserRow> getAllUsers();
    Page<UserRow> getAllUsers(int itemsPerPage, int page);
    int getUserCount();
}