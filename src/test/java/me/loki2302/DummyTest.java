package me.loki2302;

import groovy.json.JsonBuilder;
import me.loki2302.dao.PostRow;
import me.loki2302.dao.UserRow;
import me.loki2302.dao.PostDAO;
import me.loki2302.dao.UserDAO;
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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private Facade facade;

    @Autowired
    private Validator validator;

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

        UserRow userRow = userDAO.findUser(user.id);
        assertEquals((long) user.id, userRow.getId());
        assertEquals(user.name, userRow.getName());
        assertEquals(3L, userRow.getPostCount());
        assertEquals(5L, userRow.getCommentCount());

        List<PostRow> postRows = postDAO.getAll();
        assertEquals(3L, postRows.size());

        List<BriefPostDTO> posts = facade.getPosts();
        assertEquals(3L, posts.size());

        assertValid(posts.get(0));
        assertEquals(0, posts.get(0).getId());
        assertEquals("loki2302-post1", posts.get(0).getContent());
        assertEquals(5, posts.get(0).getCommentCount());
        assertEquals((long)user.id, posts.get(0).getUser().getId());
        assertEquals(user.name, posts.get(0).getUser().getName());
        assertEquals(3, posts.get(0).getUser().getPostCount());
        assertEquals(5, posts.get(0).getUser().getCommentCount());
        assertEquals(3, posts.get(0).getRecentComments().size());
        assertEquals((long)user.id, posts.get(0).getRecentComments().get(0).getUser().getId());
        assertEquals((long)4, posts.get(0).getRecentComments().get(0).getId());
        assertEquals((long)user.id, posts.get(0).getRecentComments().get(1).getUser().getId());
        assertEquals((long)3, posts.get(0).getRecentComments().get(1).getId());
        assertEquals((long)user.id, posts.get(0).getRecentComments().get(2).getUser().getId());
        assertEquals((long)2, posts.get(0).getRecentComments().get(2).getId());

        assertValid(posts.get(1));
        assertEquals(1, posts.get(1).getId());
        assertEquals("loki2302-post2", posts.get(1).getContent());
        assertEquals(0, posts.get(1).getCommentCount());
        assertEquals((long)user.id, posts.get(1).getUser().getId());
        assertEquals(user.name, posts.get(1).getUser().getName());
        assertEquals(3, posts.get(1).getUser().getPostCount());
        assertEquals(5, posts.get(1).getUser().getCommentCount());
        assertEquals(0, posts.get(1).getRecentComments().size());

        assertValid(posts.get(2));
        assertEquals(2, posts.get(2).getId());
        assertEquals("loki2302-post3", posts.get(2).getContent());
        assertEquals(0, posts.get(2).getCommentCount());
        assertEquals((long)user.id, posts.get(2).getUser().getId());
        assertEquals(user.name, posts.get(2).getUser().getName());
        assertEquals(3, posts.get(2).getUser().getPostCount());
        assertEquals(5, posts.get(2).getUser().getCommentCount());
        assertEquals(0, posts.get(2).getRecentComments().size());

        System.out.println(new JsonBuilder(posts).toPrettyString());

        UserDTO userDTO = facade.getUser(user.id);
        assertValid(userDTO);

        System.out.println(new JsonBuilder(userDTO).toPrettyString());
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

    private void assertValid(Object obj) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if(violations.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(ConstraintViolation<Object> violation : violations) {
            sb.append(String.format("\n%s[%s]::%s: %s (was %s)",
                    violation.getRootBeanClass().getSimpleName(),
                    violation.getLeafBean().getClass().getSimpleName(),
                    violation.getPropertyPath(),
                    violation.getMessage(),
                    violation.getInvalidValue()));
        }

        fail(sb.toString());
    }
}
