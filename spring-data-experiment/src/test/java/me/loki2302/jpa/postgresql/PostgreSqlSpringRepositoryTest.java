package me.loki2302.jpa.postgresql;

import me.loki2302.jpa.PostgreSqlConfiguration;
import me.loki2302.jpa.SpringRepositoryTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = PostgreSqlConfiguration.class)
public class PostgreSqlSpringRepositoryTest extends SpringRepositoryTest {	    
}