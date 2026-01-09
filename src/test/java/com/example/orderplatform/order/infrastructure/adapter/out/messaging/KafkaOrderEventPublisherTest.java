package com.example.orderplatform.order.infrastructure.adapter.out.messaging;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class KafkaOrderEventPublisherTest {

    private final KafkaOrderEventPublisher publisher = new KafkaOrderEventPublisher();
    private final Clock clock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));

    @Test
    void publishOrderCreated_ShouldNotThrow() {
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100"))), clock);
        assertDoesNotThrow(() -> publisher.publishOrderCreated(order));
    }

    @Test
    void publishOrderApproved_ShouldNotThrow() {
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100"))), clock);
        assertDoesNotThrow(() -> publisher.publishOrderApproved(order));
    }

    @Test
    void publishOrderRejected_ShouldNotThrow() {
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100"))), clock);
        assertDoesNotThrow(() -> publisher.publishOrderRejected(order));
    }
}
