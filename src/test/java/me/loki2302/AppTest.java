package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static me.loki2302.App.AppConfiguration;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AppConfiguration.class)
public class AppTest {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    public void dummy() {
        List<String> strings = jdbcTemplate.query("select * from users", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return String.format("%d %s", rs.getInt("id"), rs.getString("name"));
            }
        });

        assertEquals(3, strings.size());
        assertEquals("1 user A", strings.get(0));
        assertEquals("2 user B", strings.get(1));
        assertEquals("3 user C", strings.get(2));
    }
}
