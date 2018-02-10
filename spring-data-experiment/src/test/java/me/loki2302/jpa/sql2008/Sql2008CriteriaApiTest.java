package me.loki2302.jpa.sql2008;

import me.loki2302.jpa.CriteriaApiTest;
import me.loki2302.jpa.SqlServer2008Configuration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = SqlServer2008Configuration.class)
public class Sql2008CriteriaApiTest extends CriteriaApiTest {	    
}