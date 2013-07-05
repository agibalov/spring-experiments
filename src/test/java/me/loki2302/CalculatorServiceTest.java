package me.loki2302;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CalculatorServiceTestConfiguration.class)
public class CalculatorServiceTest {
    @Autowired
    private CalculatorService calculatorService;
    
    @Test
    public void test() {
        int result = calculatorService.addNumbers(1, 2);
        assertEquals(3, result);
    }
}
