package com.oliveyoung.sale.service;

import com.oliveyoung.sale.config.KafkaConfig;
import com.oliveyoung.sale.dto.QueueEntryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer 서비스
 *
 * 대기열 진입 요청을 Kafka 토픽에 발행
 *
 * [역할]
 * - 사용자 → API → Kafka Produce (즉시 응답)
 * - Kafka → Consumer → Redis ZADD (비동기 처리)
 *
 * [파티션 키]
 * - productId를 키로 사용
 * - 같은 상품에 대한 요청은 같은 파티션에서 순서 보장
 * - 다른 상품은 다른 파티션에서 병렬 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, QueueEntryMessage> kafkaTemplate;

    /**
     * 대기열 진입 요청을 Kafka에 발행
     *
     * @param message 대기열 진입 메시지
     */
    public void sendQueueEntry(QueueEntryMessage message) {
        String partitionKey = String.valueOf(message.getProductId());

        CompletableFuture<SendResult<String, QueueEntryMessage>> future =
                kafkaTemplate.send(KafkaConfig.QUEUE_TOPIC, partitionKey, message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka 메시지 발행 실패 - sessionId: {}, productId: {}, error: {}",
                        message.getSessionId(), message.getProductId(), ex.getMessage());
            } else {
                log.debug("Kafka 메시지 발행 성공 - topic: {}, partition: {}, offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
