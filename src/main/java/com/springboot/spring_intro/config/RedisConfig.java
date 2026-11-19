package com.springboot.spring_intro.config;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

//    @Bean
//    public RedisCacheConfiguration redisCache(ObjectMapper mapper) {
//
//        ObjectMapper redisObjMap = mapper.copy();
//        redisObjMap.registerModule(new JavaTimeModule());
//
//        GenericJackson2JsonRedisSerializer jsonRedisSerializer = GenericJackson2JsonRedisSerializer.builder()
//                .objectMapper(redisObjMap)
//                .defaultTyping(true)
//                .build();
//
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(10))
//                .serializeKeysWith(
//                        RedisSerializationContext.SerializationPair
//                                .fromSerializer(RedisSerializer.string())
//                )
//                .serializeValuesWith(
//                        RedisSerializationContext.SerializationPair
//                                .fromSerializer(RedisSerializer.json())
//                );
//    }

    @Bean
    public RedisCacheConfiguration redisCache() {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.string())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.json())
                );

    }

}
