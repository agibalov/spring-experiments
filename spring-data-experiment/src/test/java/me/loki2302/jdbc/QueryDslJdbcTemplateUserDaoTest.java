package me.loki2302.jdbc;

import me.loki2302.jdbc.querydsl.QueryDslJdbcTemplateUserDao;

import org.springframework.beans.factory.annotation.Autowired;

public class QueryDslJdbcTemplateUserDaoTest extends UserDaoTest {       
    @Autowired
    private QueryDslJdbcTemplateUserDao queryDslJdbcTemplateUserDao;   

    @Override
    protected UserDao userDao() {
        return queryDslJdbcTemplateUserDao;
    }        
}