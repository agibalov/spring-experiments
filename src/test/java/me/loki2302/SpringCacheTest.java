package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpringCacheTest {
    @Configuration
    @EnableCaching
    static class Config {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }

        @Bean
        public SlowService slowService() {
            return mock(SlowService.class);
        }

        @Bean
        public SlowServiceConsumer slowServiceConsumer() {
            return new SlowServiceConsumer();
        }
    }

    @Autowired
    private SlowService slowService;

    @Autowired
    private SlowServiceConsumer slowServiceConsumer;

    @Test
    public void slowServiceGetsOnlyAccessedOnce() {
        when(slowService.getData()).thenReturn("hello");

        assertEquals("hello", slowServiceConsumer.getData());
        assertEquals("hello", slowServiceConsumer.getData());

        verify(slowService, only()).getData();
    }

    public static class SlowServiceConsumer {
        @Autowired
        private SlowService slowService;

        @Cacheable("data")
        public String getData() {
            return slowService.getData();
        }
    }

    public static interface SlowService {
        String getData();
    }
}
