package com.oliveyoung.sale.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchaseRequest(
        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        @NotNull(message = "수량은 필수입니다")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다")
        Integer quantity,

        @NotNull(message = "대기열 토큰은 필수입니다")
        String token
) {}
