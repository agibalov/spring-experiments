package me.loki2302.jpa.postgresql;

import me.loki2302.jpa.JPQLTest;
import me.loki2302.jpa.PostgreSqlConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = PostgreSqlConfiguration.class)
public class PostgreSqlJPQLTest extends JPQLTest {	    
}