package me.loki2302.postgresql;

import me.loki2302.PostgreSqlConfiguration;
import me.loki2302.SpringRepositoryTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = PostgreSqlConfiguration.class)
public class PostgreSqlSpringRepositoryTest extends SpringRepositoryTest {	    
}