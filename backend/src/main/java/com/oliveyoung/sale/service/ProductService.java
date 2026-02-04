package com.oliveyoung.sale.service;

import com.oliveyoung.sale.domain.Product;
import com.oliveyoung.sale.dto.ProductResponse;
import com.oliveyoung.sale.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 서비스
 *
 * [면접 포인트]
 * Q: "Aurora Reader/Writer 분리는 어떻게 활용했나요?"
 * A: @Transactional(readOnly = true)가 붙은 메서드는
 *    Spring의 DataSource 라우팅으로 Reader 인스턴스로 연결됩니다.
 *
 *    구현 방법:
 *    1) AbstractRoutingDataSource 확장
 *    2) AWS RDS Proxy 사용 (connection pooling + read/write 분리)
 *    3) Spring Cloud AWS 라이브러리 활용
 *
 *    이 MVP에서는 단일 DataSource지만, 프로덕션 확장 시
 *    설정만 추가하면 분리 가능한 구조입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final SaleStateService saleStateService;

    /**
     * 전체 상품 목록 조회
     */
    public List<ProductResponse> getAllProducts() {
        boolean isSaleActive = saleStateService.isSaleActive();
        return productRepository.findAll().stream()
                .map(product -> toResponse(product, isSaleActive))
                .toList();
    }

    /**
     * 상품 상세 조회
     */
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));

        boolean isSaleActive = saleStateService.isSaleActive();
        return toResponse(product, isSaleActive);
    }

    /**
     * 재고 차감 (비관적 락 사용)
     */
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        product.decreaseStock(quantity);
        // JPA 변경 감지로 자동 UPDATE
    }

    /**
     * Product -> ProductResponse 변환
     *
     * [면접 포인트]
     * Q: "세일 상태에 따라 가격이 다른데, 프론트에서 처리하면 안 되나요?"
     * A: 절대 안 됩니다. 프론트엔드 로직은 조작 가능합니다.
     *    가격 계산은 반드시 서버에서 수행하고,
     *    결제 시에도 서버에서 다시 계산해서 검증해야 합니다.
     */
    private ProductResponse toResponse(Product product, boolean isSaleActive) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .originalPrice(product.getOriginalPrice())
                .discountedPrice(isSaleActive ? product.getDiscountedPrice() : product.getOriginalPrice())
                .discountRate(isSaleActive ? product.getDiscountRate() : 0)
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory())
                .isSaleActive(isSaleActive)
                .build();
    }
}
