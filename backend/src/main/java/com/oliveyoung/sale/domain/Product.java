package com.oliveyoung.sale.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 상품 엔티티
 *
 * [면접 포인트]
 * Q: "왜 가격을 BigDecimal로 사용했나요?"
 * A: 금융/결제 데이터는 부동소수점 오차가 치명적입니다.
 *    Double로 50000 * 0.7 계산 시 34999.999... 같은 오차 발생 가능.
 *    BigDecimal은 정확한 십진수 연산을 보장합니다.
 */
@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private Integer discountRate; // 할인율 (%)

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    @Column(nullable = false)
    private String category;

    @Builder
    public Product(String name, String description, BigDecimal originalPrice,
                   Integer discountRate, Integer stock, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountRate = discountRate;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    /**
     * 할인가 계산
     *
     * [면접 포인트]
     * Q: "할인가 계산을 엔티티에서 하는 이유는?"
     * A: 도메인 로직은 도메인 객체에 있어야 합니다 (Rich Domain Model).
     *    서비스에서 계산하면 로직이 흩어지고, 여러 곳에서 중복 계산 위험.
     */
    public BigDecimal getDiscountedPrice() {
        if (discountRate == null || discountRate == 0) {
            return originalPrice;
        }
        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discountRate)
                .divide(BigDecimal.valueOf(100));
        return originalPrice.multiply(discountMultiplier).setScale(0, java.math.RoundingMode.DOWN);
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock -= quantity;
    }
}
