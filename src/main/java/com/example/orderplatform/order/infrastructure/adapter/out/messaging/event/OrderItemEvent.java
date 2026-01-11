package com.example.orderplatform.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;

public record OrderItemEvent(
        String productId,
        int quantity,
        BigDecimal price
) {}
