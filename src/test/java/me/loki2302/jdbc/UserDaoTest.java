package me.loki2302.jdbc;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.googlecode.flyway.core.Flyway;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JdbcConfiguration.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public abstract class UserDaoTest {
    @Autowired
    private DataSource dataSource;
    
    protected abstract UserDao userDao();
    
    @Before
    public void setUpDatabase() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }
    
    @Test
    public void thereAreNoUsersInEmptyDatabase() {
        assertEquals(0, userDao().getUserCount());
        
        List<UserRow> allUsers = userDao().getAllUsers();
        assertTrue(allUsers.isEmpty());
    }
    
    @Test
    public void canCreateUser() {        
        UserRow user = userDao().createUser("loki2302");
        assertEquals(1, userDao().getUserCount());        
        assertNotEquals(0, user.UserId);
        assertEquals("loki2302", user.Name);
    }
    
    @Test
    public void createUserThrowsWhenUserNameAlreadyUsed() {
        userDao().createUser("loki2302");
        assertEquals(1, userDao().getUserCount());
        try {
            userDao().createUser("loki2302");
            fail();
        } catch(UserAlreadyExistsException e) {
            assertEquals(1, userDao().getUserCount());
        }
    }
    
    @Test
    public void canGetUserByUserId() {
        UserRow originalUser = userDao().createUser("loki2302");
        int userId = originalUser.UserId;
        UserRow retrievedUser = userDao().findUser(userId);
        assertEquals(originalUser.UserId, retrievedUser.UserId);
        assertEquals(originalUser.Name, retrievedUser.Name);
    }
    
    @Test
    public void canGetMultipleUsersByUserIds() {
        UserRow createdUsers[] = new UserRow[] {
                userDao().createUser("user1"),
                userDao().createUser("user2"),
                userDao().createUser("user3"),
                userDao().createUser("user4"),
                userDao().createUser("user5")
        };
        
        List<UserRow> retrievedUsers = userDao().findUsers(
                Arrays.asList(
                    createdUsers[0].UserId, 
                    createdUsers[2].UserId, 
                    createdUsers[3].UserId));
        assertEquals(3, retrievedUsers.size());
        assertEquals(createdUsers[0].UserId, retrievedUsers.get(0).UserId);
        assertEquals(createdUsers[0].Name, retrievedUsers.get(0).Name);
        assertEquals(createdUsers[2].UserId, retrievedUsers.get(1).UserId);
        assertEquals(createdUsers[2].Name, retrievedUsers.get(1).Name);
        assertEquals(createdUsers[3].UserId, retrievedUsers.get(2).UserId);
        assertEquals(createdUsers[3].Name, retrievedUsers.get(2).Name);
    }
    
    @Test
    public void whenThereIsNoUserWithGivenUserIdServiceReturnsNull() {
        assertNull(userDao().findUser(123));
    }
    
    @Test
    public void canGetAllUsers() {
        UserRow user1 = userDao().createUser("loki2302");
        UserRow user2 = userDao().createUser("loki2302_2");
        List<UserRow> allUsers = userDao().getAllUsers();
        assertEquals(2, allUsers.size());
        assertEquals(user1.UserId, allUsers.get(0).UserId);
        assertEquals(user1.Name, allUsers.get(0).Name);
        assertEquals(user2.UserId, allUsers.get(1).UserId);
        assertEquals(user2.Name, allUsers.get(1).Name);
    }
    
    @Test
    public void canGetAllUsersWithPagination() {
        UserRow createdUsers[] = new UserRow[] {
                userDao().createUser("user1"),
                userDao().createUser("user2"),
                userDao().createUser("user3"),
                userDao().createUser("user4"),
                userDao().createUser("user5")
        };
        
        Page<UserRow> page1 = userDao().getAllUsers(2, 0);        
        assertEquals(5, page1.TotalItems);
        assertEquals(3, page1.TotalPages);
        assertEquals(0, page1.CurrentPage);
        assertEquals(2, page1.Items.size());
        assertEquals(createdUsers[0].UserId, page1.Items.get(0).UserId);
        assertEquals(createdUsers[1].UserId, page1.Items.get(1).UserId);
        
        Page<UserRow> page2 = userDao().getAllUsers(2, 1);        
        assertEquals(5, page2.TotalItems);
        assertEquals(3, page2.TotalPages);
        assertEquals(1, page2.CurrentPage);
        assertEquals(2, page2.Items.size());
        assertEquals(createdUsers[2].UserId, page2.Items.get(0).UserId);
        assertEquals(createdUsers[3].UserId, page2.Items.get(1).UserId);
        
        Page<UserRow> page3 = userDao().getAllUsers(2, 2);        
        assertEquals(5, page3.TotalItems);
        assertEquals(3, page3.TotalPages);
        assertEquals(2, page3.CurrentPage);
        assertEquals(1, page3.Items.size());
        assertEquals(createdUsers[4].UserId, page3.Items.get(0).UserId);        
    }
}