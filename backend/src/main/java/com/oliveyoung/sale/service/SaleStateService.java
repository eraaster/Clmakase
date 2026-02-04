package com.oliveyoung.sale.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 세일 상태 관리 서비스
 *
 * [면접 포인트]
 * Q: "세일 상태를 Redis로 관리하는 이유는?"
 * A: EKS에서 여러 Pod가 실행될 때, 각 Pod의 메모리는 독립적입니다.
 *    Pod A에서 세일을 시작해도 Pod B는 모릅니다.
 *    Redis는 모든 Pod가 공유하는 중앙 저장소 역할을 합니다.
 *
 * Q: "세일 시작 시 수천 명이 동시 접속하면?"
 * A: Redis GET은 O(1) 연산으로 초당 수십만 요청 처리 가능.
 *    CloudFront 캐싱 + Redis 조회로 DB 부하를 최소화합니다.
 *    상품 목록은 CloudFront에서 TTL 캐싱,
 *    세일 상태만 Redis에서 실시간 조회합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaleStateService {

    private static final String SALE_STATE_KEY = "sale:active";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 세일 시작
     */
    public void startSale() {
        redisTemplate.opsForValue().set(SALE_STATE_KEY, true);
        log.info("🎉 세일이 시작되었습니다!");
    }

    /**
     * 세일 종료
     */
    public void endSale() {
        redisTemplate.opsForValue().set(SALE_STATE_KEY, false);
        log.info("세일이 종료되었습니다.");
    }

    /**
     * 세일 진행 중 여부 확인
     *
     * [면접 포인트]
     * Q: "Redis 연결 실패 시 어떻게 처리하나요?"
     * A: 기본값 false 반환 (세일 비활성).
     *    프로덕션에서는 Circuit Breaker로 폴백 처리하고,
     *    로컬 캐시(Caffeine)를 2차 캐시로 활용할 수 있습니다.
     */
    public boolean isSaleActive() {
        try {
            Object value = redisTemplate.opsForValue().get(SALE_STATE_KEY);
            return Boolean.TRUE.equals(value);
        } catch (Exception e) {
            log.warn("Redis 조회 실패, 기본값(false) 반환: {}", e.getMessage());
            return false;
        }
    }
}
