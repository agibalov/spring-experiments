package me.loki2302.mysql;

import me.loki2302.CriteriaApiTest;
import me.loki2302.MySqlConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MySqlConfiguration.class)
public class MySqlCriteriaApiTest extends CriteriaApiTest {	    
}