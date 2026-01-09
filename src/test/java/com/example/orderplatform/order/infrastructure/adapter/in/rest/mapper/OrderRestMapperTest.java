package com.example.orderplatform.order.infrastructure.adapter.in.rest.mapper;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.CreateOrderRequest;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.OrderItemRequest;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderRestMapperTest {

    @Test
    void toCommand_ShouldMapCorrectly() {
        // Given
        OrderItemRequest item1 = new OrderItemRequest("p1", 2, new BigDecimal("10.00"));
        OrderItemRequest item2 = new OrderItemRequest("p2", 1, new BigDecimal("50.00"));
        CreateOrderRequest request = new CreateOrderRequest(List.of(item1, item2));

        // When
        List<OrderItem> items = OrderRestMapper.toCommand(request);

        // Then
        assertEquals(2, items.size());
        assertEquals("p1", items.get(0).productId());
        assertEquals(2, items.get(0).quantity());
        assertEquals(new BigDecimal("10.00"), items.get(0).price());
        assertEquals("p2", items.get(1).productId());
        assertEquals(1, items.get(1).quantity());
        assertEquals(new BigDecimal("50.00"), items.get(1).price());
    }

    @Test
    void toResponse_ShouldMapCorrectly() {
        // Given
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));
        List<OrderItem> items = List.of(new OrderItem("p1", 2, new BigDecimal("10.00")));
        Order order = Order.create(items, fixedClock);

        // When
        OrderResponse response = OrderRestMapper.toResponse(order);

        // Then
        assertNotNull(response);
        assertEquals(order.getId(), response.id());
        assertEquals("Created", response.status());
        assertEquals(order.getCreatedAt(), response.createdAt());
        assertEquals(1, response.items().size());
        assertEquals("p1", response.items().get(0).productId());
        assertEquals(2, response.items().get(0).quantity());
        assertEquals(new BigDecimal("10.00"), response.items().get(0).price());
    }
}
