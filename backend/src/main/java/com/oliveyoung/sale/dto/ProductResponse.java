package com.oliveyoung.sale.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * 상품 응답 DTO
 *
 * [프론트엔드 연동 포인트]
 * - originalPrice: 항상 정가 표시
 * - discountedPrice: 세일 중이면 할인가, 아니면 정가와 동일
 * - discountRate: 세일 중이면 할인율(%), 아니면 0
 * - isSaleActive: 프론트에서 할인 표시 여부 결정
 */
@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal originalPrice,
        BigDecimal discountedPrice,
        Integer discountRate,
        Integer stock,
        String imageUrl,
        String category,
        boolean isSaleActive
) {}
