package me.loki2302;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(value = "me.loki2302", excludeFilters = { 
        @ComponentScan.Filter(Controller.class), 
        @ComponentScan.Filter(Configuration.class) })
public class CalculatorServiceTestConfiguration {
}
