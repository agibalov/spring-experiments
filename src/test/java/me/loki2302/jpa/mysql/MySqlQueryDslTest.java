package me.loki2302.jpa.mysql;

import me.loki2302.jpa.MySqlConfiguration;
import me.loki2302.jpa.QueryDslTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MySqlConfiguration.class)
public class MySqlQueryDslTest extends QueryDslTest {	    
}