package me.loki2302;

import org.lightadmin.api.config.AdministrationConfiguration;
import org.lightadmin.api.config.LightAdmin;
import org.lightadmin.api.config.builder.EntityMetadataConfigurationUnitBuilder;
import org.lightadmin.api.config.builder.FieldSetConfigurationUnitBuilder;
import org.lightadmin.api.config.builder.ScreenContextConfigurationUnitBuilder;
import org.lightadmin.api.config.unit.EntityMetadataConfigurationUnit;
import org.lightadmin.api.config.unit.FieldSetConfigurationUnit;
import org.lightadmin.api.config.unit.ScreenContextConfigurationUnit;
import org.lightadmin.core.config.LightAdminWebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class DummyController {
        @RequestMapping("/")
        public String index() {
            return "hello";
        }
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            LightAdmin.configure(servletContext)
                    .basePackage("me.loki2302")
                    .baseUrl("/admin")
                    .security(false)
                    .backToSiteUrl("http://localhost:8080");

            new LightAdminWebApplicationInitializer().onStartup(servletContext);
        };
    }

    @Bean(initMethod = "generateContent")
    public ContentGenerator contentGenerator() {
        return new ContentGenerator(100, 10, 1000);
    }

    public static class ContentGenerator {
        private final static Logger LOGGER = LoggerFactory.getLogger(ContentGenerator.class);

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private BlogRepository blogRepository;

        private final int numBlogs;
        private final int minPostsPerBlog;
        private final int maxPostsPerBlog;

        public ContentGenerator(int numBlogs, int minPostsPerBlog, int maxPostsPerBlog) {
            this.numBlogs = numBlogs;
            this.minPostsPerBlog = minPostsPerBlog;
            this.maxPostsPerBlog = maxPostsPerBlog;
        }

        public void generateContent() {
            Random random = new Random();

            for(int i = 0; i < numBlogs; ++i) {
                String blogName = String.format("Blog #%d", i + 1);
                Blog blog = makeBlog(blogName);

                int postCount = minPostsPerBlog + random.nextInt(maxPostsPerBlog - minPostsPerBlog + 1);
                LOGGER.info("Creating blog {} of {} with {} posts", (i+1), numBlogs, postCount);
                for(int j = 0; j < postCount; ++j) {
                    String postName = String.format("Post #%d at %s", j + 1, blogName);
                    String postContent = String.format("Content for %s", postName);
                    makePost(blog, postName, postContent);
                }
            }
        }

        private Blog makeBlog(String name) {
            Blog blog = new Blog();
            blog.name = name;
            return blogRepository.save(blog);
        }

        private Post makePost(Blog blog, String title, String content) {
            Post post = new Post();
            post.blog = blog;
            post.title = title;
            post.content = content;
            return postRepository.save(post);
        }
    }
}
