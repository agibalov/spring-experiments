package me.loki2302.jdbc;

import static com.mysema.query.types.PathMetadataFactory.*;
import static org.junit.Assert.*;

import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.Path;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.query.QueryDslJdbcTemplate;
import org.springframework.data.jdbc.query.SqlInsertCallback;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.googlecode.flyway.core.Flyway;
import com.mysema.query.sql.PrimaryKey;
import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JdbcConfiguration.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class QueryDslTest {
    @Autowired
    private DataSource dataSource;
    
    @Before
    public void setUpDatabase() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }
    
    @Test
    public void dummy() {
        QueryDslJdbcTemplate qdslTemplate = new QueryDslJdbcTemplate(dataSource);
        
        final QUser qUser = QUser.user;
        
        qdslTemplate.insert(qUser,  new SqlInsertCallback() {
            @Override
            public long doInSqlInsertClause(SQLInsertClause insert) {
                return insert
                        .columns(qUser.rowUuid, qUser.name)
                        .values(UUID.randomUUID().toString(), "lokiloki")
                        .execute();
            }            
        });
        
        SQLQuery query = qdslTemplate.newSqlQuery().from(qUser);
        List<User> users = qdslTemplate.query(query, BeanPropertyRowMapper.newInstance(User.class), qUser.id, qUser.rowUuid, qUser.name);
        System.out.println(users.size());
        assertEquals(1, users.size());
        for(User u : users) {
            System.out.println(u);
        }
        assertEquals("lokiloki", users.get(0).name);
    }
    
    public static class User {
        private Long id;
        private String rowUuid;
        private String name;
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getRowUuid() {
            return rowUuid;
        }
        
        public void setRowUuid(String rowUuid) {
            this.rowUuid = rowUuid;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class QUser extends RelationalPathBase<QUser> {
        private static final long serialVersionUID = -6373083272339779161L;
        
        public static final QUser user = new QUser("User");
        public final NumberPath<Long> id = createNumber("Id", Long.class);
        public final StringPath rowUuid = createString("RowUuid");
        public final StringPath name = createString("Name");
        
        public final PrimaryKey<QUser> pk = createPrimaryKey(id);
        
        public QUser(String variable) {
            super(QUser.class, forVariable(variable), "PUBLIC", "Users");
        }
        
        @SuppressWarnings("all")
        public QUser(Path<? extends QUser> path) {
            super((Class)path.getType(), path.getMetadata(), "PUBLIC", "Users");
        }

        public QUser(PathMetadata<?> metadata) {
            super(QUser.class, metadata, "PUBLIC", "Users");
        }        
    }
}
