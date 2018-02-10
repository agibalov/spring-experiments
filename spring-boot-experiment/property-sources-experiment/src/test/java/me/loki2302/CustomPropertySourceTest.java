package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomPropertySourceTest {
    @Autowired
    private ConfigurableEnvironment environment;

    @Value("${x}")
    private String x;

    @Test
    public void canUseCustomPropertySource() {
        assertEquals(x, "x from my property source");
    }

    @Configuration
    public static class Config {
        @Autowired
        private ConfigurableEnvironment environment;

        @PostConstruct
        public void omg() {
            environment.getPropertySources().addFirst(new MyPropertySource("omg", new Object()));
        }
    }

    public static class MyPropertySource extends EnumerablePropertySource<Object> {
        private final Map<String, String> properties = new HashMap<>();

        public MyPropertySource(String name, Object source) {
            super(name, source);
            properties.put("x", "x from my property source");
        }

        @Override
        public String[] getPropertyNames() {
            return properties.keySet().toArray(new String[properties.keySet().size()]);
        }

        @Override
        public Object getProperty(String name) {
            return properties.get(name);
        }
    }
}
