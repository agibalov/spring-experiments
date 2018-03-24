package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomSpringCacheTest {
    @Configuration
    @EnableCaching
    static class Config {
        @Bean
        public CacheManager cacheManager() {
            return new MyCacheManager();
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

    public interface SlowService {
        String getData();
    }

    public static class MyCacheManager implements CacheManager {
        private final Map<String, MyCache> cacheMap = new HashMap<>();

        @Override
        public Cache getCache(String name) {
            synchronized (cacheMap) {
                MyCache cache = cacheMap.get(name);
                if(cache == null) {
                    cache = new MyCache(name);
                    cacheMap.put(name, cache);
                }

                return cache;
            }
        }

        @Override
        public Collection<String> getCacheNames() {
            synchronized (cacheMap) {
                return Collections.unmodifiableSet(cacheMap.keySet());
            }
        }
    }

    public static class MyCache implements Cache {
        private final String name;
        private final Map<Object, Object> valueMap = new HashMap<Object, Object>();

        public MyCache(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getNativeCache() {
            return valueMap;
        }

        @Override
        public ValueWrapper get(Object key) {
            synchronized (valueMap) {
                Object value = valueMap.get(key);
                if(value == null) {
                    return null;
                }

                return new SimpleValueWrapper(value);
            }
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            synchronized (valueMap) {
                Object value = valueMap.get(key);
                if (type != null && value != null && !type.isInstance(value)) {
                    throw new IllegalStateException();
                }

                return (T) value;
            }
        }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            synchronized (valueMap) {
                Object value = valueMap.get(key);
                if(value == null) {
                    value = valueLoader.getClass();
                }

                return (T) value;
            }
        }

        @Override
        public void put(Object key, Object value) {
            synchronized (valueMap) {
                valueMap.put(key, value);
            }
        }

        @Override
        public ValueWrapper putIfAbsent(Object key, Object value) {
            synchronized (valueMap) {
                Object existing = valueMap.putIfAbsent(key, value);
                if(existing == null) {
                    return null;
                }

                return new SimpleValueWrapper(existing);
            }
        }

        @Override
        public void evict(Object key) {
            synchronized (valueMap) {
                valueMap.remove(key);
            }
        }

        @Override
        public void clear() {
            synchronized (valueMap) {
                valueMap.clear();
            }
        }
    }
}
