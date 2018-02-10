package me.loki2302.jpa.hsqldb;

import me.loki2302.jpa.HsqldbConfiguration;
import me.loki2302.jpa.QueryDslTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = HsqldbConfiguration.class)
public class HsqldbQueryDslTest extends QueryDslTest {	    
}