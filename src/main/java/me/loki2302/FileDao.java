package me.loki2302;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
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
    
    public int insertFile(String fileName, long fileSize, byte[] data) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(
                "insert into File(Name, Size, Data) " + 
                "values(:name, :size, :data)", 
                new MapSqlParameterSource()
                    .addValue("name", fileName)
                    .addValue("size", fileSize)
                    .addValue("data", data),
                keyHolder);
        
        return (Integer)keyHolder.getKey();
    }
    
    public int insertFileFromStream(
            final String fileName, 
            final long fileSize, 
            final InputStream dataStream) throws DataAccessException, IOException {
                        
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement(
                        "insert into File(Name, Size, Data) " + 
                        "values(?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
            }            
        };
        
        PreparedStatementCallback<Integer> preparedStatementCallback = new PreparedStatementCallback<Integer>() {
            @Override
            public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                ps.setString(1, fileName);
                ps.setLong(2, fileSize);
                ps.setBinaryStream(3, dataStream);
                ps.executeUpdate();
                
                ResultSet resultSet = ps.getGeneratedKeys();
                resultSet.next();
                
                return resultSet.getInt(1);
            }                    
        };
        
        return jdbcTemplate.getJdbcOperations().execute(
                preparedStatementCreator,
                preparedStatementCallback);
    }
    
    public List<FileRow> getFiles() {
        List<FileRow> fileRows = jdbcTemplate.query(
                "select Id, Name, Size from File", 
                new FileRowMapper());
        return fileRows;
    }
    
    public FileRow getFileData(int id) {
        FileRow fileRow = DataAccessUtils.singleResult(jdbcTemplate.query(
                "select Id, Size, Name from File where Id = :id", 
                new MapSqlParameterSource()
                    .addValue("id", id),
                new FileRowMapper()));
        return fileRow;
    }
    
    public void writeFileDataToOutputStream(int id, final OutputStream outputStream) {
        jdbcTemplate.query(
                "select Data from File where Id = :id", 
                new MapSqlParameterSource()
                    .addValue("id", id),
                new WriteFileDataToOutputStreamResultSetExtractor(outputStream));
    }    

    private static class FileRowMapper implements RowMapper<FileRow> {
        @Override
        public FileRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            FileRow fileRow = new FileRow();
            fileRow.Id = rs.getInt("Id");
            fileRow.Name = rs.getString("Name");
            fileRow.Size = rs.getLong("Size");
            return fileRow;
        }            
    }
    
    private static class WriteFileDataToOutputStreamResultSetExtractor implements ResultSetExtractor<Object> {
        private final OutputStream outputStream;

        public WriteFileDataToOutputStreamResultSetExtractor(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            if(!rs.next()) {
                throw new RuntimeException("no such file");
            }
            
            InputStream dataStream = rs.getBinaryStream("Data");
            try {                
                //IOUtils.copy(dataStream, outputStream);
                copySlowly(dataStream, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            
            return null;
        }
        
        private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
        
        private static long copySlowly(InputStream input, OutputStream output) throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            long count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
                
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {}                
            }
            return count;
        } 
    }
}