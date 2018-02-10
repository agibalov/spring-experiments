package me.loki2302.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import me.loki2302.jdbc.UserRow;
import me.loki2302.jdbc.UserRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

@Component
public class GetAllUsersQuery extends MappingSqlQuery<UserRow> {
    private final static String sql = 
            "select Id, Name from Users";

    @Autowired
    private UserRowMapper userRowMapper;
    
    @Autowired
    public GetAllUsersQuery(DataSource dataSource) {
        super(dataSource, sql);
    }
    
    public List<UserRow> run() {
        return execute();
    }
    
    @Override
    protected UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return userRowMapper.mapRow(rs, rowNum);
    }
}