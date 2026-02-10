package com.oliveyoung.sale.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    private Product createProduct(BigDecimal price, Integer discountRate, Integer stock) {
        return Product.builder()
                .name("테스트 상품")
                .description("테스트 설명")
                .originalPrice(price)
                .discountRate(discountRate)
                .stock(stock)
                .imageUrl("test.jpg")
                .category("스킨케어")
                .build();
    }

    @Test
    @DisplayName("할인율 30% 적용 시 할인가 계산")
    void getDiscountedPrice_withValidDiscount() {
        Product product = createProduct(BigDecimal.valueOf(18000), 30, 100);

        BigDecimal discountedPrice = product.getDiscountedPrice();

        assertThat(discountedPrice).isEqualByComparingTo(BigDecimal.valueOf(12600));
    }

    @Test
    @DisplayName("할인율 0이면 원래 가격 반환")
    void getDiscountedPrice_withZeroDiscount() {
        Product product = createProduct(BigDecimal.valueOf(18000), 0, 100);

        BigDecimal discountedPrice = product.getDiscountedPrice();

        assertThat(discountedPrice).isEqualByComparingTo(BigDecimal.valueOf(18000));
    }

    @Test
    @DisplayName("할인율 null이면 원래 가격 반환")
    void getDiscountedPrice_withNullDiscount() {
        Product product = createProduct(BigDecimal.valueOf(18000), null, 100);

        BigDecimal discountedPrice = product.getDiscountedPrice();

        assertThat(discountedPrice).isEqualByComparingTo(BigDecimal.valueOf(18000));
    }

    @Test
    @DisplayName("재고 정상 차감")
    void decreaseStock_normal() {
        Product product = createProduct(BigDecimal.valueOf(18000), 30, 100);

        product.decreaseStock(3);

        assertThat(product.getStock()).isEqualTo(97);
    }

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void decreaseStock_insufficientStock() {
        Product product = createProduct(BigDecimal.valueOf(18000), 30, 2);

        assertThatThrownBy(() -> product.decreaseStock(5))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @DisplayName("남은 재고 전부 구매 가능")
    void decreaseStock_exactStock() {
        Product product = createProduct(BigDecimal.valueOf(18000), 30, 5);

        product.decreaseStock(5);

        assertThat(product.getStock()).isEqualTo(0);
    }
}
