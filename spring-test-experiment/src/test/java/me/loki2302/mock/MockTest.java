package me.loki2302.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
public class MockTest {
    @MockBean
    private Database database;

    @SpyBean
    private Service service;

    @Autowired
    private Controller controller;

    @Test
    public void controllerShouldProvideResponse() {
        given(database.getData()).willReturn("MOCK DATA!!!");
        String response = controller.getResponse();
        assertEquals("data is MOCK DATA!!!", response);

        verify(service, times(1)).getBusinessData();
    }

    @Configuration
    public static class Config {
        @Bean
        public Controller controller() {
            return new Controller();
        }

        @Bean
        public Service service() {
            return new Service();
        }

        @Bean
        public Database database() {
            return new Database();
        }
    }

    public static class Controller {
        @Autowired
        private Service service;

        public String getResponse() {
            return String.format("data is %s", service.getBusinessData());
        }
    }

    public static class Service {
        @Autowired
        private Database database;

        public String getBusinessData() {
            return database.getData();
        }
    }

    public static class Database {
        public String getData() {
            throw new RuntimeException("Attempted to access real database from unit test!");
        }
    }
}
