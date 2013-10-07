package me.loki2302.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {
    @Autowired
    private NamedParameterJdbcTemplate template;
    
    public UserRow createUser(final String userName) {       
        int userCount = template.queryForObject(
                "select count(*) from Users where Name = :userName", 
                new MapSqlParameterSource().addValue("userName", userName),
                Integer.class);
        if(userCount > 0) {
            throw new UserAlreadyExistsException();
        }
        
        final String rowUuid = UUID.randomUUID().toString();                
        template.update(
                "insert into Users(RowUuid, Name) values(:rowUuid, :userName)",
                new MapSqlParameterSource()
                    .addValue("rowUuid", rowUuid)
                    .addValue("userName", userName));
        
        UserRow user = template.queryForObject(
                "select Id, Name from Users where RowUuid = :rowUuid",
                new MapSqlParameterSource().addValue("rowUuid", rowUuid),
                new UserRowMapper());
                
        return user;
    }
    
    public UserRow findUser(final int userId) {                
        return DataAccessUtils.singleResult(template.query(
                "select Id, Name from Users where Id = :userId",
                new MapSqlParameterSource()
                .addValue("userId", userId),
                new UserRowMapper()));
    }
       
    public List<UserRow> findUsers(final List<Integer> userIds) {               
        return template.query(
                "select Id, Name from Users where Id in (:userIds)",
                new MapSqlParameterSource().addValue("userIds", userIds),
                new UserRowMapper());
    }
        
    public List<UserRow> getAllUsers() {
        return template.query(
                "select Id, Name from Users", 
                new UserRowMapper());
    }
    
    public Page<UserRow> getAllUsers(final int itemsPerPage, final int page) {
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
                new UserRowMapper());
        return pageResult;
    }
    
    public int getUserCount() {
        return template.queryForObject(
                "select count(*) from Users",
                new MapSqlParameterSource(),
                Integer.class);
    }

    private static class UserRowMapper implements RowMapper<UserRow> {
        @Override
        public UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserRow userRow = new UserRow();
            userRow.UserId = rs.getInt("Id");
            userRow.Name = rs.getString("Name");
            return userRow;
        }
    }
    
    public class Page<T> {
        public int TotalItems;
        public int TotalPages;
        public int CurrentPage;
        public List<T> Items;
    }
}