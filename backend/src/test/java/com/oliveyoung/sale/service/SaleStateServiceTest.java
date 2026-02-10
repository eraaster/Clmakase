package com.oliveyoung.sale.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleStateServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private SaleStateService saleStateService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Redis에 true 저장 시 세일 활성 상태 반환")
    void isSaleActive_returnsTrue() {
        when(valueOperations.get("sale:active")).thenReturn(Boolean.TRUE);

        assertThat(saleStateService.isSaleActive()).isTrue();
    }

    @Test
    @DisplayName("Redis에 false 저장 시 세일 비활성 상태 반환")
    void isSaleActive_returnsFalse() {
        when(valueOperations.get("sale:active")).thenReturn(Boolean.FALSE);

        assertThat(saleStateService.isSaleActive()).isFalse();
    }

    @Test
    @DisplayName("Redis 연결 실패 시 false 반환 (폴백)")
    void isSaleActive_redisFailure_returnsFalse() {
        when(valueOperations.get("sale:active")).thenThrow(new RuntimeException("Redis 연결 실패"));

        assertThat(saleStateService.isSaleActive()).isFalse();
    }

    @Test
    @DisplayName("세일 시작 시 Redis에 true 저장")
    void startSale_setsTrue() {
        saleStateService.startSale();

        verify(valueOperations).set("sale:active", true);
    }

    @Test
    @DisplayName("세일 종료 시 Redis에 false 저장")
    void endSale_setsFalse() {
        saleStateService.endSale();

        verify(valueOperations).set("sale:active", false);
    }
}
