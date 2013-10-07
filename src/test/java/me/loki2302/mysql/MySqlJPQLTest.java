package me.loki2302.mysql;

import me.loki2302.JPQLTest;
import me.loki2302.MySqlConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MySqlConfiguration.class)
public class MySqlJPQLTest extends JPQLTest {	    
}