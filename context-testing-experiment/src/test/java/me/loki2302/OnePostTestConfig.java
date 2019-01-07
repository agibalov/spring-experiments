package me.loki2302;

import me.loki2302.impl.BlogService;
import me.loki2302.impl.entities.Post;
import me.loki2302.impl.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OneUserTestConfig.class)
public class OnePostTestConfig {
    @Autowired
    private BlogService blogService;

    @Autowired
    private User user;

    @Bean
    public Post predefinedPost() {
        return blogService.createPost(user, "dummyPost");
    }
}
