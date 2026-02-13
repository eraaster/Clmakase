package com.oliveyoung.sale.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Cache 설정 (ElastiCache)
 *
 * [캐시 전략]
 * - products:all  → 전체 상품 목록 (TTL 60초)
 * - product:{id}  → 개별 상품 상세 (TTL 60초)
 *
 * [캐시 무효화 시점]
 * - 세일 시작/종료 시 → 가격이 바뀌므로 전체 캐시 삭제
 * - 재고 변경 시 → 해당 상품 캐시 삭제
 *
 * [효과]
 * - 세일 시 /api/products 호출 폭증 → DB 대신 Redis에서 응답
 * - Aurora 부하 90% 이상 감소
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 기본 캐시 설정 (TTL 60초)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // 캐시별 TTL 개별 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        // 상품 목록: 60초 (세일 가격 반영 주기)
        cacheConfigurations.put("products", defaultConfig.entryTtl(Duration.ofSeconds(60)));
        // 개별 상품: 60초
        cacheConfigurations.put("product", defaultConfig.entryTtl(Duration.ofSeconds(60)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
