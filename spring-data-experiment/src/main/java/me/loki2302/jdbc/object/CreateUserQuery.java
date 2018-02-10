package me.loki2302.jdbc.object;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class CreateUserQuery extends SqlUpdate {
    private final static String sql = 
            "insert into Users(Name) values(:userName)";
    
    @Autowired
    public CreateUserQuery(DataSource dataSource) {
        super(dataSource, sql);
        setReturnGeneratedKeys(true);
        declareParameter(new SqlParameter(Types.VARCHAR));
        compile();
    }
    
    public int run(String userName) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("userName", userName);        
        updateByNamedParam(parameters, keyHolder);
        return (Integer)keyHolder.getKey();
    }
}