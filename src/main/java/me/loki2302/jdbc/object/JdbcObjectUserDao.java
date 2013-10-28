package me.loki2302.jdbc.object;

import java.util.List;

import me.loki2302.jdbc.Page;
import me.loki2302.jdbc.UserAlreadyExistsException;
import me.loki2302.jdbc.UserDao;
import me.loki2302.jdbc.UserRow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcObjectUserDao implements UserDao {
    @Autowired
    private GetUserCountQuery getUserCountQuery;
    
    @Autowired
    private FindUserByIdQuery findUserByIdQuery;
    
    @Autowired
    private GetAllUsersQuery getAllUsersQuery;
    
    @Autowired
    private FindUsersQuery findUsersQuery;
    
    @Autowired
    private CreateUserQuery createUserQuery;
    
    @Autowired
    private FindUserByNameQuery findUserByNameQuery;
    
    @Autowired
    private GetUsersPageQuery getUsersPageQuery;
    
    @Override
    public UserRow createUser(String userName) {
        if(findUserByNameQuery.run(userName) != null) {
            throw new UserAlreadyExistsException();
        }
        
        int userId = createUserQuery.run(userName);
        return findUser(userId);
    }

    @Override
    public UserRow findUser(int userId) {        
        return findUserByIdQuery.run(userId);
    }

    @Override
    public List<UserRow> findUsers(List<Integer> userIds) {        
        return findUsersQuery.run(userIds);
    }

    @Override
    public List<UserRow> getAllUsers() {
        return getAllUsersQuery.run();
    }

    @Override
    public Page<UserRow> getAllUsers(int itemsPerPage, int page) {
        int totalUsers = getUserCountQuery.run();
        
        Page<UserRow> pageResult = new Page<UserRow>();
        pageResult.TotalItems = totalUsers;
        pageResult.TotalPages = (totalUsers / itemsPerPage) + (totalUsers % itemsPerPage > 0 ? 1 : 0);
        pageResult.CurrentPage = page;
        pageResult.Items = getUsersPageQuery.run(page * itemsPerPage, itemsPerPage);        
        return pageResult;
    }

    @Override
    public int getUserCount() {
        return getUserCountQuery.run();
    }        
}