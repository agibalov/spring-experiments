package me.loki2302.jdbc;

import me.loki2302.jdbc.template.JdbcTemplateUserDao;

import org.springframework.beans.factory.annotation.Autowired;

public class JdbcTemplateUserDaoTest extends UserDaoTest {       
    @Autowired
    private JdbcTemplateUserDao jdbcTemplateUserDao;   

    @Override
    protected UserDao userDao() {
        return jdbcTemplateUserDao;
    }
}