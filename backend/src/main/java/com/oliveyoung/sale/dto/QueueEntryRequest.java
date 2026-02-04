package com.oliveyoung.sale.dto;

import jakarta.validation.constraints.NotNull;

public record QueueEntryRequest(
        @NotNull(message = "상품 ID는 필수입니다")
        Long productId
) {}
