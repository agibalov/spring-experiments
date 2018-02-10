package me.loki2302.jpa.mysql;

import me.loki2302.jpa.JPQLTest;
import me.loki2302.jpa.MySqlConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MySqlConfiguration.class)
public class MySqlJPQLTest extends JPQLTest {	    
}