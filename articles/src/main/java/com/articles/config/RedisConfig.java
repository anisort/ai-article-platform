package com.articles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Defines redis config.
 */
@Configuration
public class RedisConfig {

    @Bean
/**
 * Returns cache manager.
 * @param connectionFactory the connectionFactory
 * @return the result of the operation
 */
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisSerializer<Object> valueSerializer = RedisSerializer.java();
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer)
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(Map.of(
                        CacheNames.SEARCH_BY_SEMANTIC, defaultCacheConfiguration.entryTtl(Duration.ofMinutes(3)),
                        CacheNames.RECOMMENDATIONS_ANON, defaultCacheConfiguration.entryTtl(Duration.ofMinutes(5))
                ))
                .build();
    }
}
