package io.agibalov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {
    @Value("${login}")
    private String login;

    @Value("${password}")
    private String password;

    @Autowired
    private AppProperties appProperties;

    @Test
    public void test() {
        assertEquals("andreya", login);
        assertEquals("qwerty", password);

        assertEquals("the app username", appProperties.getUsername());
        assertEquals("qwerty", appProperties.getPassword());
    }
}
