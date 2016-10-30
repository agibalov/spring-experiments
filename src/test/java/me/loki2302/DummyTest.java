package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = App.class)
@RunWith(SpringRunner.class)
public class DummyTest {
    @Autowired
    private App.UpperCaseGateway upperCaseGateway;

    @Test
    public void dummy() {
        assertEquals("HELLO", upperCaseGateway.uppercase("hello"));
    }
}
