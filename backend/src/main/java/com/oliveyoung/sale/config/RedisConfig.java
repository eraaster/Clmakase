package com.oliveyoung.sale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정
 *
 * [면접 포인트]
 * Q: "왜 Redis를 사용했나요?"
 * A: 1) 세일 상태(ON/OFF)는 모든 서버가 공유해야 하는 글로벌 상태입니다.
 *       In-Memory는 서버별로 분리되어 일관성 보장 불가.
 *    2) 대기열은 Sorted Set으로 O(log N) 순위 조회가 가능합니다.
 *    3) ElastiCache는 AWS 관리형으로 클러스터링, 복제, 자동 복구 지원.
 *
 * Q: "Redis가 죽으면 어떻게 되나요?"
 * A: ElastiCache Multi-AZ 구성으로 자동 페일오버.
 *    추가로 Circuit Breaker 패턴으로 폴백 처리 가능.
 *    (Resilience4j 또는 Spring Retry 사용)
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 String으로
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value는 JSON으로 (디버깅 편의성)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}
