package me.loki2302.jdbc.querydsl;

import java.sql.SQLException;
import java.util.List;

import me.loki2302.jdbc.Page;
import me.loki2302.jdbc.UserAlreadyExistsException;
import me.loki2302.jdbc.UserDao;
import me.loki2302.jdbc.UserRow;
import me.loki2302.jdbc.UserRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.query.QueryDslJdbcTemplate;
import org.springframework.data.jdbc.query.SqlInsertWithKeyCallback;
import org.springframework.stereotype.Repository;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.types.Expression;

@Repository
public class QueryDslJdbcTemplateUserDao implements UserDao {
    private final static QUserRow qUserRow = QUserRow.userRow;    
    private final static Expression<?>[] userRowProjection = new Expression<?>[] { 
        qUserRow.id, 
        qUserRow.name };
    
    @Autowired
    private QueryDslJdbcTemplate queryDslJdbcTemplate;
    
    @Autowired
    private UserRowMapper userRowMapper;
    
    @Override
    public UserRow createUser(final String userName) {        
        int userCount = (int)queryDslJdbcTemplate.count(
                queryDslJdbcTemplate.newSqlQuery()
                    .from(qUserRow)
                    .where(qUserRow.name.eq(userName))); 
        
        if(userCount > 0) {
            throw new UserAlreadyExistsException();
        }
                
        int userId = queryDslJdbcTemplate.insertWithKey(qUserRow, new SqlInsertWithKeyCallback<Integer>() {
            @Override
            public Integer doInSqlInsertWithKeyClause(SQLInsertClause insert) throws SQLException {
                return insert
                        .columns(qUserRow.name)
                        .values(userName)
                        .executeWithKey(qUserRow.id);
            }            
        });
        
        return findUser(userId);
    }

    @Override
    public UserRow findUser(int userId) {        
        SQLQuery query = queryDslJdbcTemplate
                .newSqlQuery()
                .from(qUserRow)
                .where(qUserRow.id.eq(userId));
        
        return queryDslJdbcTemplate.queryForObject(
                query, 
                userRowMapper, 
                userRowProjection);
    }

    @Override
    public List<UserRow> findUsers(List<Integer> userIds) {        
        SQLQuery query = queryDslJdbcTemplate
                .newSqlQuery()
                .from(qUserRow)
                .where(qUserRow.id.in(userIds));
        
        return queryDslJdbcTemplate.query(
                query, 
                userRowMapper, 
                userRowProjection);
    }

    @Override
    public List<UserRow> getAllUsers() {        
        SQLQuery query = queryDslJdbcTemplate
                .newSqlQuery()
                .from(qUserRow);
        
        return queryDslJdbcTemplate.query(
                query, 
                userRowMapper, 
                userRowProjection);
    }

    @Override
    public Page<UserRow> getAllUsers(int itemsPerPage, int page) {        
        int totalUsers = getUserCount();
        
        Page<UserRow> pageResult = new Page<UserRow>();
        pageResult.TotalItems = totalUsers;
        pageResult.TotalPages = (totalUsers / itemsPerPage) + (totalUsers % itemsPerPage > 0 ? 1 : 0);
        pageResult.CurrentPage = page;
        
        SQLQuery query = queryDslJdbcTemplate
                .newSqlQuery()
                .from(qUserRow)
                .offset(page * itemsPerPage)
                .limit(itemsPerPage);
        
        pageResult.Items = queryDslJdbcTemplate.query(
                query, 
                userRowMapper, 
                userRowProjection);
        
        return pageResult;
    }

    @Override
    public int getUserCount() {
        SQLQuery query = queryDslJdbcTemplate
                .newSqlQuery()
                .from(qUserRow);
        
        return (int)queryDslJdbcTemplate.count(query);
    }
}
