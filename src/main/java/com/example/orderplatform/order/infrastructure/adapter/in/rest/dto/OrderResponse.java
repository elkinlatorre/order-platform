package com.example.orderplatform.order.infrastructure.adapter.in.rest.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String status,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
