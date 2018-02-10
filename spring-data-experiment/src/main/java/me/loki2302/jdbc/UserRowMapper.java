package me.loki2302.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class UserRowMapper implements RowMapper<UserRow> {    
    @Override
    public UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserRow userRow = new UserRow();
        userRow.UserId = rs.getInt("Id");
        userRow.Name = rs.getString("Name");
        return userRow;
    }
}