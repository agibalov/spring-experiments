package me.loki2302.jdbc;

import me.loki2302.jdbc.object.JdbcObjectUserDao;

import org.springframework.beans.factory.annotation.Autowired;

public class JdbcObjectUserDaoTest extends UserDaoTest {
    @Autowired
    private JdbcObjectUserDao sqlObjectUserDao;   

    @Override
    protected UserDao userDao() {
        return sqlObjectUserDao;
    }        
}