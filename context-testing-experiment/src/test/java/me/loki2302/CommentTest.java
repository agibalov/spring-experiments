package me.loki2302;

import me.loki2302.impl.AppConfig;
import me.loki2302.impl.BlogService;
import me.loki2302.impl.entities.Comment;
import me.loki2302.impl.entities.Post;
import me.loki2302.impl.repositories.CommentRepository;
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

public class CommentTest {
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
        public void canCreateComment() {
            Comment comment = blogService.createComment(predefinedPost.id, "hi there");
            assertNotNull(comment.id);
            assertEquals(predefinedPost.id, comment.post.id);
        }
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @IntegrationTest
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = {AppConfig.class, OneCommentTestConfig.class})
    public static class GivenThereIsAComment {
        @Autowired
        private Comment predefinedComment;

        @Autowired
        private CommentRepository commentRepository;

        @Autowired
        private BlogService blogService;

        @Test
        public void canDeleteComment() {
            blogService.deleteComment(predefinedComment.id);
            assertEquals(0, commentRepository.count());

            // TODO: userA->post1 + userB->post1->comment1
        }
    }
}
