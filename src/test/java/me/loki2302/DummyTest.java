package me.loki2302;

import me.loki2302.entities.Comment;
import me.loki2302.entities.Post;
import me.loki2302.entities.User;
import me.loki2302.repositories.CommentRepository;
import me.loki2302.repositories.PostRepository;
import me.loki2302.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@SpringApplicationConfiguration(classes = Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DummyTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserDAO userDAO;

    @Test
    public void hello() {
        User user = user("loki2302");
        Post post = post(user, "loki2302-post1");
        post(user, "loki2302-post2");
        post(user, "loki2302-post3");

        comment(user, post, "loki2302-post1-comment1");
        comment(user, post, "loki2302-post1-comment2");
        comment(user, post, "loki2302-post1-comment3");
        comment(user, post, "loki2302-post1-comment4");
        comment(user, post, "loki2302-post1-comment5");

        UserDTO userDTO = userDAO.findUser(0);
        assertEquals(0L, userDTO.getId());
        assertEquals("loki2302", userDTO.getName());
        assertEquals(3L, userDTO.getPostCount());
        assertEquals(5L, userDTO.getCommentCount());
    }

    private User user(String name) {
        User user = new User();
        user.name = "loki2302";
        user = userRepository.save(user);
        return user;
    }

    private Post post(User user, String content) {
        Post post = new Post();
        post.user = user;
        post.content = content;
        post = postRepository.save(post);
        return post;
    }

    private Comment comment(User user, Post post, String content) {
        Comment comment = new Comment();
        comment.user = user;
        comment.post = post;
        comment.content = content;
        comment = commentRepository.save(comment);
        return comment;
    }
}
