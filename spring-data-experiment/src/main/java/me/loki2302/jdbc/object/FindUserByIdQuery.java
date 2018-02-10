package me.loki2302.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import me.loki2302.jdbc.UserRow;
import me.loki2302.jdbc.UserRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

@Component
public class FindUserByIdQuery extends MappingSqlQuery<UserRow> {
    private final static String sql = 
            "select Id, Name from Users where Id = :id";
    
    @Autowired
    private UserRowMapper userRowMapper;

    @Autowired
    public FindUserByIdQuery(DataSource dataSource) {
        super(dataSource, sql);
        declareParameter(new SqlParameter(Types.INTEGER));
        compile();
    }
    
    public UserRow run(int userId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", userId);
        return DataAccessUtils.singleResult(executeByNamedParam(parameters));
    }
    
    @Override
    protected UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return userRowMapper.mapRow(rs, rowNum);
    }
}