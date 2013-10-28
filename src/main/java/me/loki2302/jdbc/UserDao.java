package me.loki2302.jdbc;

import java.util.List;

public interface UserDao {
    public UserRow createUser(String userName);
    public UserRow findUser(int userId);
    public List<UserRow> findUsers(List<Integer> userIds);
    public List<UserRow> getAllUsers();
    public Page<UserRow> getAllUsers(int itemsPerPage, int page);
    public int getUserCount();
}