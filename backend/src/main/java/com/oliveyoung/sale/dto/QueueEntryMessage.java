package com.oliveyoung.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kafka 대기열 진입 메시지
 *
 * Producer → Kafka Topic → Consumer → Redis ZADD
 *
 * productId를 파티션 키로 사용하여
 * 같은 상품에 대한 요청은 같은 파티션에서 순서 보장
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueueEntryMessage {
    private String sessionId;
    private Long productId;
    private String token;
    private long timestamp;
}
