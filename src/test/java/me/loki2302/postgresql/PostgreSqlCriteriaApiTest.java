package me.loki2302.postgresql;

import me.loki2302.CriteriaApiTest;
import me.loki2302.PostgreSqlConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = PostgreSqlConfiguration.class)
public class PostgreSqlCriteriaApiTest extends CriteriaApiTest {	    
}