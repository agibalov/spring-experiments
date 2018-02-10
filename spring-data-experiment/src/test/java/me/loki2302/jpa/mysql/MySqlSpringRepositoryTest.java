package me.loki2302.jpa.mysql;

import me.loki2302.jpa.MySqlConfiguration;
import me.loki2302.jpa.SpringRepositoryTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MySqlConfiguration.class)
public class MySqlSpringRepositoryTest extends SpringRepositoryTest {	    
}