package me.loki2302.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

@Component
public class GetUserCountQuery extends MappingSqlQuery<Integer> {
    private final static String sql = 
            "select count(Id) as UserCount from Users";
    
    @Autowired
    public GetUserCountQuery(DataSource dataSource) {
        super(dataSource, sql);
    }
    
    public int run() {
        return findObject();
    }

    @Override
    protected Integer mapRow(ResultSet rs, int rowNum) throws SQLException {            
        return rs.getInt("UserCount");
    }
}