package com.oliveyoung.sale.dto;

import java.math.BigDecimal;

public record PurchaseResponse(
        Long orderId,
        String productName,
        Integer quantity,
        BigDecimal totalPrice,
        String message
) {}
