package com.example.orderplatform.order.infrastructure.adapter.out.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderCreatedEvent(
        String orderId,
        Instant createdAt,
        BigDecimal totalAmount,
        List<OrderItemEvent> items
) {}
