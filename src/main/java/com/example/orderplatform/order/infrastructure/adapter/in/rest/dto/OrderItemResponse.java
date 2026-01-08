package com.example.orderplatform.order.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        int quantity,
        BigDecimal price
) {
}
