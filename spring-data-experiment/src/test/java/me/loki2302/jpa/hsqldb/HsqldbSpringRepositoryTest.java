package me.loki2302.jpa.hsqldb;

import me.loki2302.jpa.HsqldbConfiguration;
import me.loki2302.jpa.SpringRepositoryTest;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = HsqldbConfiguration.class)
public class HsqldbSpringRepositoryTest extends SpringRepositoryTest {	    
}