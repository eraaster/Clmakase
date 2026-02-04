package com.oliveyoung.sale.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 엔티티
 *
 * [면접 포인트]
 * Q: "Order 대신 PurchaseOrder로 명명한 이유는?"
 * A: Order는 SQL 예약어입니다. JPA에서 테이블명 충돌 방지를 위해
 *    명시적인 네이밍을 사용했습니다.
 */
@Entity
@Table(name = "purchase_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId; // 사용자 식별 (시연용 간소화)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Builder
    public PurchaseOrder(String sessionId, Product product, Integer quantity,
                         BigDecimal totalPrice, OrderStatus status) {
        this.sessionId = sessionId;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public enum OrderStatus {
        PENDING,    // 대기 중
        COMPLETED,  // 완료
        CANCELLED   // 취소
    }
}
