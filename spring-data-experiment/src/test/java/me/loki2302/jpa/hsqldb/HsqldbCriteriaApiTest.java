package me.loki2302.jpa.hsqldb;

import me.loki2302.jpa.CriteriaApiTest;
import me.loki2302.jpa.HsqldbConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = HsqldbConfiguration.class)
public class HsqldbCriteriaApiTest extends CriteriaApiTest {	    
}