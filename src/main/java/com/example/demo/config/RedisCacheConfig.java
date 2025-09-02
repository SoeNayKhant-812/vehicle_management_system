package com.example.demo.config;

import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {
	
	// Cache names for Cars
    public static final String CARS_CACHE = "cars";
    public static final String CAR_CACHE = "car";
    
    // Cache names for Users
    public static final String USERS_CACHE = "users";
    public static final String USER_CACHE = "user";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, UserService userService) {

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .computePrefixWith(cacheName -> "vms:cache:" + cacheName + "::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));
        
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

//        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
//                CARS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)),
//                CAR_CACHE, defaultConfig.entryTtl(Duration.ofHours(1))
//        );

        // Configuration for Car caches
        cacheConfigurations.put(CARS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put(CAR_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));

        // Configuration for User caches
        cacheConfigurations.put(USERS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put(USER_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();

        return new LoggingCacheManager(redisCacheManager, userService);
    }
}
