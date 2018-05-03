package me.loki2302.cascading;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CascadingTest {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void dummy() {
        Blog blog = new Blog();

        Post post1 = new Post();
        post1.blog = blog;
        blog.posts.add(post1);

        Post post2 = new Post();
        post2.blog = blog;
        blog.posts.add(post2);

        blog = blogRepository.save(blog);

        assertNotNull(blog.id);

        assertNotNull(blog.posts.get(0).id);
        assertEquals(blog.id, blog.posts.get(0).blog.id);

        assertNotNull(blog.posts.get(1).id);
        assertEquals(blog.id, blog.posts.get(1).blog.id);

        assertEquals(1, blogRepository.count());
        assertEquals(2, postRepository.count());

        //

        blogRepository.delete(blog);

        assertEquals(0, blogRepository.count());
        assertEquals(0, postRepository.count());
    }
}
