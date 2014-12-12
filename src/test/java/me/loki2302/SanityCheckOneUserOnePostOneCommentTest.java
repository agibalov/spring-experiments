package me.loki2302;

import me.loki2302.impl.AppConfig;
import me.loki2302.impl.entities.Comment;
import me.loki2302.impl.entities.Post;
import me.loki2302.impl.entities.User;
import me.loki2302.impl.repositories.CommentRepository;
import me.loki2302.impl.repositories.PostRepository;
import me.loki2302.impl.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AppConfig.class, OneCommentTestConfig.class})
public class SanityCheckOneUserOnePostOneCommentTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private User predefinedUser;

    @Autowired
    private Post predefinedPost;

    @Autowired
    private Comment predefinedComment;

    @Test
    public void thereIsOneUserOnePostAndOneComment() {
        assertEquals(1, userRepository.count());
        assertEquals(1, postRepository.count());
        assertEquals(1, commentRepository.count());

        User theOnlyUser = userRepository.findAll().get(0);
        assertEquals(predefinedPost.user.id, theOnlyUser.id);

        Post theOnlyPost = postRepository.findAll().get(0);
        assertEquals(predefinedPost.id, theOnlyPost.id);

        Comment theOnlyComment = commentRepository.findAll().get(0);
        assertEquals(predefinedComment.id, theOnlyComment.id);
    }
}
