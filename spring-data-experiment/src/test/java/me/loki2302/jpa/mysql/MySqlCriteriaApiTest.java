package me.loki2302.jpa.mysql;

import me.loki2302.jpa.CriteriaApiTest;
import me.loki2302.jpa.MySqlConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MySqlConfiguration.class)
public class MySqlCriteriaApiTest extends CriteriaApiTest {	    
}