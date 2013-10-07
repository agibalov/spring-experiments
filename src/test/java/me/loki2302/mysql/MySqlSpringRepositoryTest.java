package me.loki2302.mysql;

import me.loki2302.MySqlConfiguration;
import me.loki2302.SpringRepositoryTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MySqlConfiguration.class)
public class MySqlSpringRepositoryTest extends SpringRepositoryTest {	    
}