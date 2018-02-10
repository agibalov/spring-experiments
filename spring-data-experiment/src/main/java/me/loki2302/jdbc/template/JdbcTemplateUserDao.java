package me.loki2302.jdbc.template;

import java.util.List;

import me.loki2302.jdbc.Page;
import me.loki2302.jdbc.UserAlreadyExistsException;
import me.loki2302.jdbc.UserDao;
import me.loki2302.jdbc.UserRow;
import me.loki2302.jdbc.UserRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateUserDao implements UserDao {
    @Autowired
    private NamedParameterJdbcTemplate template;
    
    @Autowired
    private UserRowMapper userRowMapper;
    
    @Override
    public UserRow createUser(String userName) {       
        int userCount = template.queryForObject(
                "select count(*) from Users where Name = :userName", 
                new MapSqlParameterSource().addValue("userName", userName),
                Integer.class);
        if(userCount > 0) {
            throw new UserAlreadyExistsException();
        }
        
        KeyHolder keyHolder = new GeneratedKeyHolder();        
                       
        template.update(
                "insert into Users(Name) values(:userName)",
                new MapSqlParameterSource().addValue("userName", userName),
                keyHolder);
        
        int userId = (Integer)keyHolder.getKey();
        
        UserRow user = template.queryForObject(
                "select Id, Name from Users where Id = :userId",
                new MapSqlParameterSource().addValue("userId", userId),
                userRowMapper);
                
        return user;
    }
    
    @Override
    public UserRow findUser(int userId) {                
        return DataAccessUtils.singleResult(template.query(
                "select Id, Name from Users where Id = :userId",
                new MapSqlParameterSource()
                .addValue("userId", userId),
                userRowMapper));
    }
       
    @Override
    public List<UserRow> findUsers(List<Integer> userIds) {               
        return template.query(
                "select Id, Name from Users where Id in (:userIds)",
                new MapSqlParameterSource().addValue("userIds", userIds),
                userRowMapper);
    }
        
    @Override
    public List<UserRow> getAllUsers() {
        return template.query(
                "select Id, Name from Users", 
                userRowMapper);
    }
    
    @Override
    public Page<UserRow> getAllUsers(int itemsPerPage, int page) {
        int totalUsers = getUserCount();
        
        Page<UserRow> pageResult = new Page<UserRow>();
        pageResult.TotalItems = totalUsers;
        pageResult.TotalPages = (totalUsers / itemsPerPage) + (totalUsers % itemsPerPage > 0 ? 1 : 0);
        pageResult.CurrentPage = page;
        pageResult.Items = template.query(
                "select limit :skip :take Id, Name from Users",
                new MapSqlParameterSource()
                    .addValue("skip", page * itemsPerPage)
                    .addValue("take", itemsPerPage),
                userRowMapper);
        return pageResult;
    }
    
    @Override
    public int getUserCount() {
        return template.queryForObject(
                "select count(*) from Users",
                new MapSqlParameterSource(),
                Integer.class);
    }
}