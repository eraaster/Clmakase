package com.oliveyoung.sale.repository;

import com.oliveyoung.sale.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * 상품 Repository
 *
 * [면접 포인트]
 * Q: "Aurora Reader/Writer 분리는 어떻게 활용했나요?"
 * A: 읽기 전용 쿼리(@Transactional(readOnly=true))는 Reader 엔드포인트로,
 *    쓰기 쿼리는 Writer 엔드포인트로 라우팅합니다.
 *    Spring의 AbstractRoutingDataSource를 확장하거나,
 *    AWS RDS Proxy의 read/write 분리 기능을 활용할 수 있습니다.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    /**
     * 비관적 락을 사용한 상품 조회 (재고 차감 시 사용)
     *
     * [면접 포인트]
     * Q: "왜 비관적 락을 사용했나요?"
     * A: 대규모 세일에서 동시에 수백 명이 같은 상품을 구매하면
     *    Lost Update 문제가 발생합니다. 비관적 락은 DB 레벨에서
     *    동시성을 제어해 정합성을 보장합니다.
     *
     *    단, 락 경합이 심하면 성능 저하가 발생합니다.
     *    이를 완화하기 위해 앞단에 Redis 대기열을 두어
     *    DB 접근 자체를 순차적으로 제어합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);
}
