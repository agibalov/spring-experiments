package me.loki2302.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import me.loki2302.jdbc.UserRow;
import me.loki2302.jdbc.UserRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

@Component
public class GetUsersPageQuery extends MappingSqlQuery<UserRow> {
    private final static String sql = 
            "select limit :skip :take Id, Name from Users";
    
    @Autowired
    private UserRowMapper userRowMapper;

    @Autowired
    public GetUsersPageQuery(DataSource dataSource) {
        super(dataSource, sql);
        declareParameter(new SqlParameter(Types.INTEGER));
        declareParameter(new SqlParameter(Types.INTEGER));
        compile();
    }
    
    public List<UserRow> run(int skip, int take) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("skip", skip);
        parameters.put("take", take);
        return executeByNamedParam(parameters);
    }
    
    @Override
    protected UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return userRowMapper.mapRow(rs, rowNum);
    }
}