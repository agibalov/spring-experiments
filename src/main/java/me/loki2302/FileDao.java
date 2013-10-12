package me.loki2302;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class FileDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    public int insertFile(String fileName, byte[] data) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(
                "insert into File(Name, Data) " + 
                "values(:name, :data)", 
                new MapSqlParameterSource()
                    .addValue("name", fileName)
                    .addValue("data", data),
                keyHolder);
        
        return (Integer)keyHolder.getKey();
    }
    
    public List<FileRow> getFiles() {
        List<FileRow> fileRows = jdbcTemplate.query(
                "select Id, Name from File", 
                new FileRowMapper());
        return fileRows;
    }
    
    public FileDataRow getFileData(int id) {
        FileDataRow fileDataRow = DataAccessUtils.singleResult(jdbcTemplate.query(
                "select Id, Name, Data from File where Id = :id", 
                new MapSqlParameterSource().addValue("id", id),
                new FileDataRowMapper()));
        return fileDataRow;
    }
    
    private static class FileRowMapper implements RowMapper<FileRow> {
        @Override
        public FileRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            FileRow fileRow = new FileRow();
            fileRow.Id = rs.getInt("Id");
            fileRow.Name = rs.getString("Name");
            return fileRow;
        }            
    }
    
    private static class FileDataRowMapper implements RowMapper<FileDataRow> {
        @Override
        public FileDataRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            FileDataRow fileDataRow = new FileDataRow();
            fileDataRow.Id = rs.getInt("Id");
            fileDataRow.Name = rs.getString("Name");
            fileDataRow.Data = rs.getBytes("Data");
            return fileDataRow;
        }            
    }
}