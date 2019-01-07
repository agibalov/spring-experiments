package me.loki2302;

import me.loki2302.impl.BlogService;
import me.loki2302.impl.entities.Comment;
import me.loki2302.impl.entities.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OnePostTestConfig.class)
public class OneCommentTestConfig {
    @Autowired
    private BlogService blogService;

    @Autowired
    private Post post;

    @Bean
    public Comment predefinedComment() {
        return blogService.createComment(post.id, "dummy comment");
    }
}
