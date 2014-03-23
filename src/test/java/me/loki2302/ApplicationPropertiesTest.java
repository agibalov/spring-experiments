package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AppConfiguration.class)
public class ApplicationPropertiesTest {
    @Autowired
    private AppProperties appProperties;

    @Test
    public void appPropertiesAreProperlyPopulated() {
        assertEquals("hello from app.properties", appProperties.getMessage());
    }
}
