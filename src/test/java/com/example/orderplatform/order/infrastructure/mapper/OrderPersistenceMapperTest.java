package com.example.orderplatform.order.infrastructure.mapper;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.example.orderplatform.order.domain.status.Approved;
import com.example.orderplatform.order.domain.status.Created;
import com.example.orderplatform.order.domain.status.PendingReview;
import com.example.orderplatform.order.domain.status.Rejected;
import com.example.orderplatform.order.infrastructure.adapter.out.persistence.OrderEntity;
import com.example.orderplatform.order.infrastructure.adapter.out.persistence.OrderItemEmbeddable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderPersistenceMapperTest {

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        List<OrderItem> items = List.of(new OrderItem("p1", 2, new BigDecimal("10.00")));
        Order order = Order.rehydrate(id, items, now);
        order.restoreStatus(new Approved());

        // When
        OrderEntity entity = OrderPersistenceMapper.toEntity(order);

        // Then
        assertEquals(id, entity.getId());
        assertEquals("APPROVED", entity.getStatus());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(1, entity.getItems().size());
        assertEquals("p1", entity.getItems().get(0).getProductId());
        assertEquals(2, entity.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("10.00"), entity.getItems().get(0).getPrice());
    }

    @Test
    void toDomain_ShouldMapCorrectly() {
        // Given
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        OrderItemEmbeddable item = new OrderItemEmbeddable("p1", 2, new BigDecimal("10.00"));
        OrderEntity entity = new OrderEntity(id, List.of(item), "PENDING_REVIEW", now);

        // When
        Order order = OrderPersistenceMapper.toDomain(entity);

        // Then
        assertEquals(id, order.getId());
        assertInstanceOf(PendingReview.class, order.getStatus());
        assertEquals(now, order.getCreatedAt());
        assertEquals(1, order.getItems().size());
        assertEquals("p1", order.getItems().get(0).productId());
    }

    @Test
    void toDomain_ShouldMapAllStatuses() {
        assertStatusMapping("CREATED", Created.class);
        assertStatusMapping("APPROVED", Approved.class);
        assertStatusMapping("PENDING_REVIEW", PendingReview.class);
        assertStatusMapping("REJECTED", Rejected.class);
    }

    private void assertStatusMapping(String statusName, Class<?> statusClass) {
        OrderEntity entity = new OrderEntity(UUID.randomUUID(), List.of(), statusName, Instant.now());
        Order order = OrderPersistenceMapper.toDomain(entity);
        assertInstanceOf(statusClass, order.getStatus());
    }

    @Test
    void toDomain_ShouldThrowExceptionForUnknownStatus() {
        OrderEntity entity = new OrderEntity(UUID.randomUUID(), List.of(), "UNKNOWN", Instant.now());
        assertThrows(IllegalStateException.class, () -> OrderPersistenceMapper.toDomain(entity));
    }
}
