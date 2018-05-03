package me.loki2302.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcTemplateTest {
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void canQueryForRows() {
        note("note one");
        note("note two");

        List<NoteRow> noteRows = jdbcTemplate.query("select * from Note", new RowMapper<NoteRow>() {
            @Override
            public NoteRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                NoteRow row = new NoteRow();
                row.id = rs.getLong("id");
                row.text = rs.getString("text");
                return row;
            }
        });

        assertEquals(2, noteRows.size());
        assertEquals(1, noteRows.get(0).id);
        assertEquals("note one", noteRows.get(0).text);
        assertEquals(2, noteRows.get(1).id);
        assertEquals("note two", noteRows.get(1).text);
    }

    @Test
    public void canQueryForSingleValue() {
        note("note one");
        note("note two");

        long count = jdbcTemplate.queryForObject("select count(*) from Note", Long.class);
        assertEquals(2, count);
    }

    private Note note(String text) {
        Note note = new Note();
        note.text = text;
        return noteRepository.save(note);
    }

    public static class NoteRow {
        public long id;
        public String text;
    }
}
