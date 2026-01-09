package com.example.orderplatform.order.domain.model;

import com.example.orderplatform.order.domain.status.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2024-01-01T00:00:00Z"),
            ZoneId.of("UTC"));

    @Test
    void shouldCreateOrderWithValidItems() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 2, new BigDecimal("50.00")));

        // When
        Order order = Order.create(items, fixedClock);

        // Then
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(1, order.getItems().size());
        assertInstanceOf(Created.class, order.getStatus());
        assertEquals(fixedClock.instant(), order.getCreatedAt());
    }

    @Test
    void shouldThrowExceptionWhenItemsAreNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(null, fixedClock));
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenItemsAreEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Order.create(new ArrayList<>(), fixedClock));
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    void shouldCalculateTotalAmountWithSingleItem() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 3, new BigDecimal("25.00")));
        Order order = Order.create(items, fixedClock);

        // When
        BigDecimal total = order.totalAmount();

        // Then
        assertEquals(new BigDecimal("75.00"), total);
    }

    @Test
    void shouldCalculateTotalAmountWithMultipleItems() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 2, new BigDecimal("50.00")),
                new OrderItem("PROD-002", 3, new BigDecimal("30.00")),
                new OrderItem("PROD-003", 1, new BigDecimal("20.00")));
        Order order = Order.create(items, fixedClock);

        // When
        BigDecimal total = order.totalAmount();

        // Then
        assertEquals(new BigDecimal("210.00"), total);
    }

    @Test
    void shouldApproveOrderWhenTotalIsLessThanOrEqualTo1000() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 10, new BigDecimal("100.00")));
        Order order = Order.create(items, fixedClock);

        // When
        order.approve();

        // Then
        assertInstanceOf(Approved.class, order.getStatus());
    }

    @Test
    void shouldApproveOrderWhenTotalIsExactly1000() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 10, new BigDecimal("100.00")));
        Order order = Order.create(items, fixedClock);

        // When
        order.approve();

        // Then
        assertInstanceOf(Approved.class, order.getStatus());
        assertEquals(new BigDecimal("1000.00"), order.totalAmount());
    }

    @Test
    void shouldSetPendingReviewWhenTotalIsGreaterThan1000() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 11, new BigDecimal("100.00")));
        Order order = Order.create(items, fixedClock);

        // When
        order.approve();

        // Then
        assertInstanceOf(PendingReview.class, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenApprovingNonCreatedOrder() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 5, new BigDecimal("100.00")));
        Order order = Order.create(items, fixedClock);
        order.approve(); // Now it's Approved

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.approve());
        assertEquals("Only CREATED orders can be approved", exception.getMessage());
    }

    @Test
    void shouldRejectOrderWithReason() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 5, new BigDecimal("100.00")));
        Order order = Order.create(items, fixedClock);
        String reason = "Insufficient inventory";

        // When
        order.reject(reason);

        // Then
        assertInstanceOf(Rejected.class, order.getStatus());
        assertEquals(reason, ((Rejected) order.getStatus()).reason());
    }

    @Test
    void shouldRejectPendingReviewOrder() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 15, new BigDecimal("100.00")));
        Order order = Order.create(items, fixedClock);
        order.approve(); // Now it's PendingReview
        String reason = "Manual review failed";

        // When
        order.reject(reason);

        // Then
        assertInstanceOf(Rejected.class, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRejectingApprovedOrder() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 5, new BigDecimal("100.00")));
        Order order = Order.create(items, fixedClock);
        order.approve(); // Now it's Approved

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.reject("Some reason"));
        assertEquals("Approved orders cannot be rejected", exception.getMessage());
    }

    @Test
    void shouldRehydrateOrderCorrectly() {
        // Given
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 2, new BigDecimal("50.00")));
        Instant createdAt = Instant.parse("2024-01-15T10:30:00Z");

        // When
        Order order = Order.rehydrate(orderId, items, createdAt);

        // Then
        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals(1, order.getItems().size());
        assertEquals(createdAt, order.getCreatedAt());
    }

    @Test
    void shouldRestoreStatusCorrectly() {
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 2, new BigDecimal("50.00")));
        Order order = Order.create(items, fixedClock);
        OrderStatus approvedStatus = new Approved();

        // When
        order.restoreStatus(approvedStatus);

        // Then
        assertInstanceOf(Approved.class, order.getStatus());
    }

    @Test
    void shouldReturnImmutableItemsList() {
        // Given
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("PROD-001", 2, new BigDecimal("50.00")));
        Order order = Order.create(items, fixedClock);

        // When
        List<OrderItem> retrievedItems = order.getItems();

        // Then
        assertThrows(UnsupportedOperationException.class,
                () -> retrievedItems.add(new OrderItem("PROD-002", 1, new BigDecimal("10.00"))));
    }

    @Test
    void shouldCalculateTotalAmountAsZeroWhenNoValidItems() {
        // This test verifies edge case behavior
        // Given
        List<OrderItem> items = List.of(
                new OrderItem("PROD-001", 1, new BigDecimal("0.01")));
        Order order = Order.create(items, fixedClock);

        // When
        BigDecimal total = order.totalAmount();

        // Then
        assertEquals(new BigDecimal("0.01"), total);
    }
}
