package me.loki2302.hsqldb;

import me.loki2302.CriteriaApiTest;
import me.loki2302.HsqldbConfiguration;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = HsqldbConfiguration.class)
public class HsqldbCriteriaApiTest extends CriteriaApiTest {	    
}