package com.oliveyoung.sale.service;

import com.oliveyoung.sale.domain.Product;
import com.oliveyoung.sale.dto.ProductResponse;
import com.oliveyoung.sale.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SaleStateService saleStateService;

    @InjectMocks
    private ProductService productService;

    private Product createProduct(Long id, BigDecimal price, Integer discountRate) {
        Product product = Product.builder()
                .name("테스트 상품")
                .description("설명")
                .originalPrice(price)
                .discountRate(discountRate)
                .stock(100)
                .imageUrl("test.jpg")
                .category("스킨케어")
                .build();
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    @Test
    @DisplayName("세일 활성 시 할인가로 상품 목록 반환")
    void getAllProducts_saleActive_showsDiscountedPrices() {
        Product product = createProduct(1L, BigDecimal.valueOf(18000), 30);
        when(saleStateService.isSaleActive()).thenReturn(true);
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).discountedPrice()).isEqualByComparingTo(BigDecimal.valueOf(12600));
        assertThat(result.get(0).discountRate()).isEqualTo(30);
        assertThat(result.get(0).isSaleActive()).isTrue();
    }

    @Test
    @DisplayName("세일 비활성 시 원래 가격으로 상품 목록 반환")
    void getAllProducts_saleInactive_showsOriginalPrices() {
        Product product = createProduct(1L, BigDecimal.valueOf(18000), 30);
        when(saleStateService.isSaleActive()).thenReturn(false);
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).discountedPrice()).isEqualByComparingTo(BigDecimal.valueOf(18000));
        assertThat(result.get(0).discountRate()).isEqualTo(0);
        assertThat(result.get(0).isSaleActive()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생")
    void getProduct_notFound_throwsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("상품 상세 조회 시 정상 반환")
    void getProduct_found_returnsCorrectDto() {
        Product product = createProduct(1L, BigDecimal.valueOf(25000), 20);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(saleStateService.isSaleActive()).thenReturn(true);

        ProductResponse result = productService.getProduct(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("테스트 상품");
        assertThat(result.discountedPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));
    }
}
