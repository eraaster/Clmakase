package com.oliveyoung.sale.service;

import com.oliveyoung.sale.domain.Product;
import com.oliveyoung.sale.domain.PurchaseOrder;
import com.oliveyoung.sale.dto.PurchaseRequest;
import com.oliveyoung.sale.dto.PurchaseResponse;
import com.oliveyoung.sale.repository.ProductRepository;
import com.oliveyoung.sale.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurchaseOrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private QueueService queueService;

    @Mock
    private SaleStateService saleStateService;

    @InjectMocks
    private PurchaseService purchaseService;

    private Product createProduct(Long id, BigDecimal price, Integer discountRate, Integer stock) {
        Product product = Product.builder()
                .name("테스트 상품")
                .description("설명")
                .originalPrice(price)
                .discountRate(discountRate)
                .stock(stock)
                .imageUrl("test.jpg")
                .category("스킨케어")
                .build();
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    @Test
    @DisplayName("세일 활성 시 할인가로 구매 성공")
    void purchase_success_withSaleActive() {
        // given
        PurchaseRequest request = new PurchaseRequest(1L, 2, "token-abc");
        Product product = createProduct(1L, BigDecimal.valueOf(18000), 30, 100);

        when(queueService.getQueueStatus("session-1", "token-abc", 1L))
                .thenReturn(new QueueService.QueueStatus(0, 0, true, false));
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(product));
        when(saleStateService.isSaleActive()).thenReturn(true);
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 1L);
            return order;
        });

        // when
        PurchaseResponse result = purchaseService.purchase("session-1", "token-abc", request);

        // then
        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.quantity()).isEqualTo(2);
        assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(25200)); // 12600 * 2
        assertThat(product.getStock()).isEqualTo(98);
        verify(queueService).completeProcessing("session-1", "token-abc", 1L);
    }

    @Test
    @DisplayName("세일 비활성 시 원가로 구매 성공")
    void purchase_success_withSaleInactive() {
        // given
        PurchaseRequest request = new PurchaseRequest(1L, 1, "token-abc");
        Product product = createProduct(1L, BigDecimal.valueOf(18000), 30, 100);

        when(queueService.getQueueStatus("session-1", "token-abc", 1L))
                .thenReturn(new QueueService.QueueStatus(0, 0, true, false));
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(product));
        when(saleStateService.isSaleActive()).thenReturn(false);
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 2L);
            return order;
        });

        // when
        PurchaseResponse result = purchaseService.purchase("session-1", "token-abc", request);

        // then
        assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(18000));
    }

    @Test
    @DisplayName("구매 불가 상태일 때 예외 발생")
    void purchase_fails_whenCannotPurchase() {
        PurchaseRequest request = new PurchaseRequest(1L, 1, "token-abc");

        when(queueService.getQueueStatus("session-1", "token-abc", 1L))
                .thenReturn(new QueueService.QueueStatus(5, 1, false, false));

        assertThatThrownBy(() -> purchaseService.purchase("session-1", "token-abc", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("구매할 수 없습니다");
    }

    @Test
    @DisplayName("상품을 찾을 수 없을 때 예외 발생")
    void purchase_fails_whenProductNotFound() {
        PurchaseRequest request = new PurchaseRequest(99L, 1, "token-abc");

        when(queueService.getQueueStatus("session-1", "token-abc", 99L))
                .thenReturn(new QueueService.QueueStatus(0, 0, true, false));
        when(productRepository.findByIdWithLock(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.purchase("session-1", "token-abc", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }
}
