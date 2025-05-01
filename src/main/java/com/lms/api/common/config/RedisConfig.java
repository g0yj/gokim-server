package com.lms.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redisTemplate의 반환타입이 <String,Object> 라면 GenericJackson2JsonRedisSerializer 반드시 추가해야함
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer (문자열)
        template.setKeySerializer(new StringRedisSerializer());

        // Hash key serializer
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value serializer (Object → JSON)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash value serializer (Object → JSON)
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}

