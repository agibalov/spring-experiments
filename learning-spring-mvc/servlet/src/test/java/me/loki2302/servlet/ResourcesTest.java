package me.loki2302.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ResourcesTest {
    @Test
    public void shouldAllowMeToGetStaticResources() {
        RestTemplate restTemplate = new RestTemplate();
        assertEquals("hey there i am 1.txt",
                restTemplate.getForObject("http://localhost:8080/test", String.class));
        assertEquals("hey there i am 1.txt",
                restTemplate.getForObject("http://localhost:8080/some/other/file.txt", String.class));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
                    resourceHandlerRegistry
                            .addResourceHandler("/**")
                            .addResourceLocations("omg")
                            .resourceChain(true)
                            .addResolver(new MyResourceResolver());

                }
            };
        }
    }

    public static class MyResourceResolver implements ResourceResolver {
        private final static Logger LOGGER = LoggerFactory.getLogger(MyResourceResolver.class);

        @Nullable
        @Override
        public Resource resolveResource(
                @Nullable HttpServletRequest request,
                String requestPath,
                List<? extends Resource> locations,
                ResourceResolverChain chain) {

            LOGGER.info("resolveResource() called! requestPath={}, locations={}", requestPath, locations);

            // can't use ByteArrayResource here, because the calling code requires the Resource to have a filename
            return new ClassPathResource("1.txt");
        }

        @Nullable
        @Override
        public String resolveUrlPath(
                String resourcePath,
                List<? extends Resource> locations,
                ResourceResolverChain chain) {

            LOGGER.info("resolveUrlPath() called! resourcePath={}, locations={}");
            return null;
        }
    }
}
