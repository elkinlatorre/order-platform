package com.example.orderplatform.order.infrastructure.adapter.out.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record OrderApprovedEvent(
        UUID orderId,
        Instant occurredAt
) {}
