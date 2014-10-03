package me.loki2302.onetomany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
public class OneToManyTest {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private PostRepository postRepository;

    @Before
    public void cleanUp() {
        postRepository.deleteAll();
        blogRepository.deleteAll();
    }

    @Test
    public void canHaveOneToMany() {
        Blog blog1 = blog();
        Post blog1post1 = post(blog1);
        Post blog1post2 = post(blog1);

        Blog blog2 = blog();
        Post blog2post1 = post(blog2);
        Post blog2post2 = post(blog2);
        Post blog2post3 = post(blog2);

        assertEquals(2, blogRepository.findAll().size());
        assertEquals(5, postRepository.findAll().size());

        blog1 = blogRepository.findOne(blog1.id);
        assertEquals(2, blog1.posts.size());

        blog2 = blogRepository.findOne(blog2.id);
        assertEquals(3, blog2.posts.size());

        blog1post1 = postRepository.findOne(blog1post1.id);
        assertEquals(blog1.id, blog1post1.blog.id);
    }

    private Blog blog() {
        Blog blog = new Blog();
        return blogRepository.save(blog);
    }

    private Post post(Blog blog) {
        Post post = new Post();
        post.blog = blog;
        return postRepository.save(post);
    }
}
