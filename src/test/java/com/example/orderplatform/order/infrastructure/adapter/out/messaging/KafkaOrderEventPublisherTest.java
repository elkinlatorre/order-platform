package com.example.orderplatform.order.infrastructure.adapter.out.messaging;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class KafkaOrderEventPublisherTest {

    private final KafkaTemplate<String, byte[]> kafkaTemplate = Mockito.mock(KafkaTemplate.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final KafkaOrderEventPublisher publisher =
            new KafkaOrderEventPublisher(kafkaTemplate, objectMapper);

    private final Clock clock =
            Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));

    @Test
    void publishOrderCreated_ShouldNotThrow() {
        Order order = Order.create(
                List.of(new OrderItem("p1", 1, new BigDecimal("100"))),
                clock
        );

        assertDoesNotThrow(() -> publisher.publishOrderCreated(order));
    }

    @Test
    void publishOrderApproved_ShouldNotThrow() {
        Order order = Order.create(
                List.of(new OrderItem("p1", 1, new BigDecimal("100"))),
                clock
        );

        assertDoesNotThrow(() -> publisher.publishOrderApproved(order));
    }

    @Test
    void publishOrderRejected_ShouldNotThrow() {
        Order order = Order.create(
                List.of(new OrderItem("p1", 1, new BigDecimal("100"))),
                clock
        );

        assertDoesNotThrow(() -> publisher.publishOrderRejected(order));
    }
}

