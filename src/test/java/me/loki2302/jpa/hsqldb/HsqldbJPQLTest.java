package me.loki2302.jpa.hsqldb;

import me.loki2302.jpa.HsqldbConfiguration;
import me.loki2302.jpa.JPQLTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = HsqldbConfiguration.class)
public class HsqldbJPQLTest extends JPQLTest {	    
}