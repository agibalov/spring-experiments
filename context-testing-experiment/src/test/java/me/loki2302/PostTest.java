package me.loki2302;

import me.loki2302.impl.AppConfig;
import me.loki2302.impl.BlogService;
import me.loki2302.impl.entities.Post;
import me.loki2302.impl.entities.User;
import me.loki2302.impl.repositories.PostRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PostTest {
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @IntegrationTest
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = {AppConfig.class, OneUserTestConfig.class})
    public static class GivenThereIsAUser {
        @Autowired
        private User predefinedUser;

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private BlogService blogService;

        @Test
        public void canCreatePost() {
            Post post = blogService.createPost(predefinedUser, "test post");
            assertNotNull(post.id);
            assertEquals(1, postRepository.count());
        }
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @IntegrationTest
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = {AppConfig.class, OnePostTestConfig.class})
    public static class GivenThereIsAPost {
        @Autowired
        private Post predefinedPost;

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private BlogService blogService;

        @Test
        public void canDeletePost() {
            blogService.deletePost(predefinedPost.id);
            assertEquals(0, postRepository.count());
        }
    }
}
