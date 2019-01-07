package me.loki2302;

import me.loki2302.impl.BlogService;
import me.loki2302.impl.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OneUserTestConfig {
    @Autowired
    private BlogService blogService;

    @Bean
    public User predefinedUser() {
        return blogService.createUser("dummyUser");
    }
}
