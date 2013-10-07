package me.loki2302.hsqldb;

import me.loki2302.HsqldbConfiguration;
import me.loki2302.QueryDslTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = HsqldbConfiguration.class)
public class HsqldbQueryDslTest extends QueryDslTest {	    
}