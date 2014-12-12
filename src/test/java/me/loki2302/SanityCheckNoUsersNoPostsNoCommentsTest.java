package me.loki2302;

import me.loki2302.impl.AppConfig;
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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AppConfig.class)
public class SanityCheckNoUsersNoPostsNoCommentsTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void thereAreNoUsersNoPostsAndNoComments() {
        assertEquals(0, userRepository.count());
        assertEquals(0, postRepository.count());
        assertEquals(0, commentRepository.count());
    }
}
